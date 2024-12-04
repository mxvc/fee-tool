import {
    CloudUploadOutlined,
    DeleteOutlined,
    FileExcelOutlined,
    InboxOutlined,
    MergeCellsOutlined,
    SyncOutlined
} from '@ant-design/icons'
import {Button, Form, Input, message, Modal, Space, Table, Tag, Typography, Upload} from 'antd'
import React from 'react'

import hutool from "@moon-cn/hutool";



export default class extends React.Component {

    state = {
        formValues: {},

        list: [],

        selectedRowKeys: [],


        uploadOpen: false,
        linkOpen: false,


        loading:false
    }


    columns = [
        {
            title: '名称',
            dataIndex: 'name',
        },

        {
            title: '类型',
            dataIndex: 'type',

            hideInSearch: true,
            render(v, record) {
                let label = record.typeLabel || '';
                if (label) {
                    label = ' (' + label + ' ) '
                }
                return record.type + label;
            }
        },

        {
            title: '代码',
            dataIndex: 'code',


        },

        {
            title: '号码',
            dataIndex: 'number',
        },

        {
            title: '发票日期',
            dataIndex: 'date',
            valueType: 'date',
            hideInSearch: true
        },

        {
            title: '校验码',
            dataIndex: 'validateCode', hideInSearch: true
        },

        {
            title: '税率',
            dataIndex: 'rate', hideInSearch: true
        },

        {
            title: '总金额, 不含税',
            dataIndex: 'amt', hideInSearch: true
        },

        {
            title: '税',
            dataIndex: 'taxAmt', hideInSearch: true
        },

        {
            title: '总金额, 含税',
            dataIndex: 'totalAmt', hideInSearch: true
        },

        {
            title: '查验结果',
            dataIndex: 'checkStatus',
            width: 200,
            render(_, record) {
               const  r = record.checkRecord
                if(r == null){
                    return <Tag> 未查验</Tag>
                }

                let v = r.status
                if (v === 'PENDING') {
                  return  <Tag> {r.statusLabel}</Tag>
                }

                if (v === 'PROCESSING') {
                    return <> <SyncOutlined spin/> {r.statusLabel}</>
                }

                if (v === "SUCCESS") {
                    return <Tag color='green'>{r.statusLabel}</Tag>
                }

                if (v === 'ERROR') {
                    return <>
                        <Tag color='red'> {r.statusLabel}</Tag>
                        <div>
                            <Typography.Text>{r.message}</Typography.Text>
                        </div>
                    </>
                }
                return v;
            }
        },


    ]

    componentDidMount() {
        this.loadData()
    }

    loadData = () => {

        this.setState({list: [],loading:true})
        hutool.http.get("invoice/page").then(rs => {
            this.setState({list: rs.data})
        }).finally(()=>{
            this.setState({loading:false})
        })
    }
    handleUpload = () => {
        this.setState({uploadOpen: true})
    }

    handleExport = () => {
        hutool.http.downloadFile('invoice/exportExcel', this.getParams()).then(rs => {

        })
    }

    getParams() {
        return {ids: this.state.selectedRowKeys.join(',')};
    }

    handleMergePdf = () => {
        hutool.http.downloadFile('invoice/exportMergePdf', this.getParams()).then(rs => {

        })
    }
    handleDelete = () => {
        hutool.http.get('invoice/delete', this.getParams()).then(rs => {
            this.loadData()
        })
    }
    handleExportCheckPDF = () => {
        hutool.http.downloadFile('invoice/exportCheckPDF', this.getParams()).then(rs => {

        })
    }
    handleCheck = () => {
        hutool.http.get('invoice/check', this.getParams()).then(rs => {
            message.success(rs.message)
            this.loadData()
            setTimeout(this.loadData, 3000)
        })
    }

    render() {
        const keys = this.state.selectedRowKeys
        const empty = keys.length === 0;
        return <div style={{padding: 12, background: '#f5f5f5', minHeight: '100vh'}}>

            <div style={{display: 'flex', justifyContent: 'space-between'}}>


                <Space>

                    <Button type='primary' icon={<CloudUploadOutlined/>} onClick={this.handleUpload}>上传</Button>


                    <Button disabled={empty} onClick={this.handleExport} icon={<FileExcelOutlined/>} >
                        导出Excel
                    </Button>

                    <Button onClick={this.handleMergePdf}
                            disabled={empty}
                            icon={<MergeCellsOutlined/>}
                            title='方便打印'>
                        导出PDF
                    </Button>

                    <Button onClick={this.handleDelete} disabled={empty} icon={<DeleteOutlined/>}>删除</Button>
                </Space>
                <Space>

                    <Typography.Text>用户：{hutool.http.getGlobalHeaders()['uid']}</Typography.Text>
                </Space>
            </div>


            <Table
                style={{marginTop: 12}}
                dataSource={this.state.list}
                columns={this.columns}
                rowKey='id'
                pagination={false}
                rowSelection={{
                    onChange: (selectedRowKeys) => {
                        this.setState({selectedRowKeys})
                    }
                }}
                scroll={{x: 'max-content'}}
                size='small'
            />


            <Modal title='上传发票' open={this.state.uploadOpen} destroyOnClose
                   onCancel={() => this.setState({uploadOpen: false})} footer={null}>
                <Upload.Dragger
                    maxCount={100}
                    action='invoice/upload'
                    showUploadList={true}
                    accept='.pdf'
                    multiple
                    withCredentials
                    headers={hutool.http.getGlobalHeaders()}
                    onChange={(info) => {

                        if (info.file.status !== 'uploading') {
                            console.log(info.file, info.fileList);
                        }
                        if (info.file.status === 'done') {
                            console.log('done', info)
                            if (info.file.response.success) {
                                message.success(`${info.file.response.message}`);
                            } else {
                                Modal.error({content: info.file.response.message})
                            }
                            this.loadData()
                        } else if (info.file.status === 'error') {
                            message.error(`${info.file.name} file upload failed.`);
                        }
                    }}
                >
                    <p className="ant-upload-drag-icon">
                        <InboxOutlined/>
                    </p>
                    <p className="ant-upload-text">上传发票</p>
                    <p className="ant-upload-hint">
                        单击或拖动发票到此区域进行上传，请勿上传公司机密数据。
                    </p>

                </Upload.Dragger>

            </Modal>

            <Modal title='短信、邮件解析' open={this.state.linkOpen} destroyOnClose
                   onCancel={() => this.setState({linkOpen: false})} footer={null}>

                <Form layout={"vertical"} onFinish={(values) => {

                    hutool.http.postForm('/invoice/parseLink', values).then(rs => {
                        message.success(rs.message)
                        this.loadData()
                    })
                }}>
                    <Form.Item label='包含发票链接的内容，如短信、邮件等' name='content' rules={[{required: true}]}>
                        <Input.TextArea rows={5}
                                        placeholder='中国石油用户您好，您收到一张新的电子发票，戳我直达：https://dlj.51fapiao.cn/dlj/v7/b7d82b25020631b6333af4f0b043c8325ce23d，会员可前往“中油好客APP-我的-发票”查看、下载'>
                        </Input.TextArea>


                    </Form.Item>
                    <Button htmlType='submit' type='primary'>确定</Button>


                </Form>


            </Modal>





        </div>
    }
}



