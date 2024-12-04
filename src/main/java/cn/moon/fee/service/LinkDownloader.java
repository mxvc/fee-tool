package cn.moon.fee.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LinkDownloader {

    public static Map<String, byte[]> download(String content) throws IOException {
        // 定义一个正则表达式来匹配URL
        String urlPattern = "https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

        // 编译正则表达式为Pattern对象
        Pattern pattern = Pattern.compile(urlPattern);

        // 创建Matcher对象来查找字符串中的匹配项
        Matcher matcher = pattern.matcher(content);

        Map<String, byte[]> result = new HashMap<>();
        // 循环查找所有匹配的URL
        while (matcher.find()) {
            // 打印出找到的URL
            String url = matcher.group();

            byte[] bytes = process(url);

            result.put(url, bytes);
        }

        return result;
    }

    private static byte[] process(String url) throws IOException {
        log.info("url: {}", url);

        if(url.contains("https://dlj.51fapiao.cn")){
            url = "https://dlj.51fapiao.cn/dlj/v7/downloadFile/" + StrUtil.subAfter(url, "/", true);
        }


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HttpUtil.download(url, os, false);

        byte[] bytes = os.toByteArray();

        os.close();

        return bytes;
    }

    public static void main(String[] args) throws IOException {
        String text = "【中国石油】中国石油用户您好，您收到一张新的电子发票，戳我直达：https://dlj.51fapiao.cn/dlj/v7/b7d82b25020631b6333af4f0b043c8325ce23d，会员可前往“中油好客APP-我的-发票”查看、下载";


        download(text);
    }
}
