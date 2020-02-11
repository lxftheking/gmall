package com.atguigu.gmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;


import com.atguigu.gmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016101500692496";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC5OXEgJHnhkuhitNEQNtocoARuO2Dvsdi7K0sRxfePQBBGUfK5CaSQePQ/TLbP/KwGWR6Ea/Z2PiCV9dE3VeSLYtNuAD+AYejXaTnWpbLVpNRplbSZc0kNl8KP5osBv2oDWJx9XaOXRINPYcfzv5c1GoVJT2BzpoZBRHzZPsgPC/nbcKRvF3ngiyb+GEn3lP5KCnvXcGqw0o46ajhYDjMF/ZTZKG6t5FTyza+ipb9v3uKvAqiymc82ETSQ5+ZG4laJD838UErlOg4F3t07Hu0zKH3GJOtBTLUZ5BYxGlm0SWzZg4Yg2q9xJgTDjbwRiFdI5oC447HUo1GV/TCCMtq5AgMBAAECggEBAINrwlxwBRqMRd5jNUMv6CoPT5V3BByOL5z95tHBiRlW9zUtx+6KbP0lFQgkwkFoohYO/ZwvjjvvcOd19vAwPWy4vhN6kZPh5HeS7bri7rSvsxcHZZDLP7YpS58WkOK4kdu177Lz0bLupcyZMGo+Mmc3XgLaEzF7oh6FHaB27zPcroI2vhZnVG6XpM/h//f0TASxTMyLwVfjUGl/p0QyFoQVN0quZ16Cjs31VXka0HF+dPjfH3IB/C5595NZH/VJBUM2Ym2lOw+CLgOUxVP9nClqD3yQP5fy3C86ij80yhGEyvyUjF8wmL1Y3NSMTI01ZkB2CG1oUb3kPQq+zIdImFECgYEA/3XWimlyRATfs7hw2yDlySFkiTWbsMUlJyBrlnYuCyuSrdTFhZM3QFYzMSYzDhaoaOj19pZnEYUz/42Ox3CJH4yl9pLZuVq30o73yDxRdxRUjezDgr48C79Er9e545JS4O9+4HhlVt87OM8UoXNKAxVPIL/9Y54UHblVBTmjmVcCgYEAuZ2eJuX4eugtck5FXzyTkiDNI0odwt0B5clq6GsEoWDAECHA8//0EhOQGmKMlN975Vo1srLRQwGr0tV+2NyY4w+NVMIaAhxk9RL0KGRfIgDutck32lFqRCz6phd+Z1fxrVAtsKBOGqKDN7LMeKnndQDaKUYf/AnpMDZDZxUW0m8CgYEAohW0UBHjjEDDZxaJlJ9k7J6hWWWIKTqM/ixV1xuYz0GQSXcM9FmZ3tpSlMa18iRyKk5VcWWwc69edLDufKBsgHYO/0nhlvd2VsSbdpK+5Z0ioQIucpAdLR7oh+GKclfu+gU7LVV2hw4QV1Ucs+ugdCBbGl52eXRdMxbaWPxfTvUCgYB1voOFf1p5dPDnUrl06XNfPgmDOk/YveOnRXbYypJcYIhVc2P4JERKdzhxVFSDV6U7X1cy7XYLgHLjwmS94mnll4b63korTqHISOay/MDQTDEhBey1Q/IaH0I8vCiGG+aGH5dsNLR29uIwIe7NGFJoCUta5U4CzrKqz7FAIcOhZwKBgGhzRYDatE3xDX8IJhukXummGJ2nNNVOHudODqstY3debWbLThQyCJ/hdrRwppSuxza4A2EVl9WxDL28G7/EoIHuTOPj9jtqWoGjhc25N4JKLKikCFFdGmMfbf46LNYUP315ES+HohBCFYBoyKYhxYwkXH7ULUqK8yjUjKDq4WbM";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlDFwIZIDbtcubjDvU+EKpmyNkEJYQbGozG6XaaNZZp7j6OB/NoDsK8LViz5yR/vTM39GzL4WnxFRV1tOyzkoMwiE6osvrUqIVGxPJ5IYFl0VNSltU4gtTYe2RsC0XCVgd24EPqevpVAcHMvkPPerdqwgGyii9rXS+WFBytjRavVx4t1CqGUdXZOpRw00r0PzwCezj2QdT6Hzuc851aU9Hq7HzdEkRY8vVDaCHDT9+OsvAJneXUPa2GqEmnRho44iwJrrDE/cFsYyfIaHEK9AH0AM9xujQpHzwsuqiw6YbwPqICHiilvok/gEUdqrKANXTM41+9xa6nExIFL17CwXgQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody(); // 二维码支付表单

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
