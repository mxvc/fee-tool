package io.github.mxvc.fee.controller;

import cn.hutool.core.util.StrUtil;
import cn.moon.lang.web.Result;
import cn.moon.lang.web.ServletTool;
import io.github.mxvc.fee.UserTool;
import io.github.mxvc.fee.entity.Invoice;
import io.github.mxvc.fee.service.ExportExcelHandler;
import io.github.mxvc.fee.service.InvoiceMergeHandler;
import io.github.mxvc.fee.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Slf4j
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

        String filename = String.format("合并发票_方便打印_%d张.pdf", list.size());
        ServletTool.setDownloadFileHeader(filename, response);

        ServletOutputStream os = response.getOutputStream();
        invoiceMergeHandler.start(list, os);
        os.flush();
        os.close();

        log.info("导出完成");
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

