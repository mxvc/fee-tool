import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import zhCN from 'antd/locale/zh_CN';
// for date-picker i18n
import 'dayjs/locale/zh-cn';
import {ConfigProvider, message} from "antd";
import hutool from "@moon-cn/hutool";

hutool.http.init({
    errorMessageHandler: msg=>message.error(msg)
})

// 生成唯一标识
if(hutool.http.getGlobalHeaders()['uid'] == null){
    hutool.http.setGlobalHeader('uid', hutool.uid())
}

ReactDOM.createRoot(document.getElementById('root')).render(
    <ConfigProvider locale={zhCN}>

        <App />
    </ConfigProvider>
)
