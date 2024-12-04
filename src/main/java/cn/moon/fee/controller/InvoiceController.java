package cn.moon.fee.controller;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.moon.fee.UserTool;
import cn.moon.fee.entity.Invoice;
import cn.moon.fee.service.*;
import cn.moon.fee.tool.PdfTool;
import cn.moon.lang.web.Result;
import cn.moon.lang.web.ServletTool;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("invoice")
public class InvoiceController {

    @Resource
    InvoiceService service;


    @Resource
    ExportExcelHandler exportExcelHandler;

    @Resource
    InvoiceMergeHandler invoiceMergeHandler;




    @GetMapping({"page"})
    public Result page(Invoice param) {
        param.setOwner(UserTool.cur());
        List<Invoice> page = this.service.findAll(param);
        return Result.ok().data(page);
    }


    @PostMapping("upload")
    public Result upload(@RequestPart("file") MultipartFile file) throws Exception {
        try {
            byte[] bytes = file.getBytes();
             service.saveUpload(bytes, file.getOriginalFilename(), UserTool.cur());

            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.err().msg("上传失败:" + file.getName() + e.getMessage());
        }

    }

    @PostMapping("parseLink")
    public Result upload(String content) throws Exception {
        if(StrUtil.isBlank(content)){
            return Result.err().msg("内容不能为空");
        }

        Map<String, byte[]> map = LinkDownloader.download(content);
        for (Map.Entry<String, byte[]> e : map.entrySet()) {
            String url = e.getKey();

            String name = FileNameUtil.mainName(url);
            if(name == null){
                name = StrUtil.subAfter(url, "/",true);
            }

            service.saveUpload(e.getValue(), name, UserTool.cur());
        }


        return Result.ok();

    }


    @GetMapping({"delete"})
    public Result delete(String ids) {
        for (Integer id : parseIds(ids)) {
            this.service.deleteById(id);
        }

        return Result.ok().msg("删除成功");
    }



    @GetMapping("exportExcel")
    public void exportExcel(String ids, HttpServletResponse response) throws Exception {
        ServletTool.setDownloadFileHeader("发票清单.xlsx", response);

        List<Invoice> list = service.findAllById(parseIds(ids));

        exportExcelHandler.exportExcel(list, response.getOutputStream());

    }



    @GetMapping("exportMergePdf")
    public void mergePdf(String ids, HttpServletResponse response) throws Exception {
        List<Invoice> list = service.findAllById(parseIds(ids));

        ServletTool.setDownloadFileHeader(String.format("合并发票_方便打印_%d张.pdf", list.size()), response);

        invoiceMergeHandler.start(list, response.getOutputStream());
    }




    public List<Integer> parseIds(String ids) {
        int[] ints = StrUtil.splitToInt(ids, ",");
        Assert.state(ints.length > 0, "请先选择数据");
        List<Integer> list = new ArrayList<>();
        for (int anInt : ints) {
            list.add(anInt);
        }
        return list;
    }



}

