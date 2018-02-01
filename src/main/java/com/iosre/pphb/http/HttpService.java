package com.iosre.pphb.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by XuJijun on 2017-06-06.
 */
@Service
public class HttpService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final ObjectMapper objectMapper = new ObjectMapper();

    CloseableHttpClient httpClient;

    public HttpService() {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        //客户端和服务器建立连接的timeout
        requestConfigBuilder.setConnectTimeout(60000);
        //从连接池获取连接的timeout
        requestConfigBuilder.setConnectionRequestTimeout(60000);
        //连接建立后，request没有回应的timeout
        requestConfigBuilder.setSocketTimeout(60000);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
        clientBuilder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(60000).build()); //连接建立后，request没有回应的timeout
        clientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
        this.httpClient = clientBuilder.build();
    }

    public HttpService(int timeout) {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        //客户端和服务器建立连接的timeout
        requestConfigBuilder.setConnectTimeout(timeout);
        //从连接池获取连接的timeout
        requestConfigBuilder.setConnectionRequestTimeout(timeout);
        //连接建立后，request没有回应的timeout
        requestConfigBuilder.setSocketTimeout(timeout);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
        clientBuilder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(timeout).build()); //连接建立后，request没有回应的timeout
        clientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
        this.httpClient = clientBuilder.build();
    }

    /**
     * 使用缺省的配置发送GET请求
     *
     * @param url
     * @return
     */
    public HttpResult get(String url) {
        return this.get(url, null);
    }

    /**
     * 使用缺省的配置发送GET请求
     *
     * @param url
     * @param params
     * @return
     */
    public HttpResult get(String url, Map<String, String> params) {
        return this.get(url, params, null);
    }

    /**
     * 使用缺省的配置发送GET请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public HttpResult get(String url, Map<String, String> params, Map<String, String> headers) {
        //设置请求参数到url中
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> paramList = new ArrayList<>();
            for (String pKey : params.keySet()) {
                paramList.add(new BasicNameValuePair(pKey, params.get(pKey)));
            }
            try {
                url = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(paramList, "UTF-8"));
            } catch (IOException e) {
                logger.error("创建参数时发生错误", e);
            }
        }

        HttpGet request = new HttpGet(url);
        //logger.debug("url: {}", url);

        //设置headers
        if (headers != null && !headers.isEmpty()) {
            for (String hKey : headers.keySet()) {
                request.addHeader(hKey, headers.get(hKey));
            }
        }

        return this.executeRequest(request);
    }

    /**
     * 使用缺省的配置发送GET请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public HttpResult getGzip(String url, Map<String, String> params, Map<String, String> headers) {
        //设置请求参数到url中
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> paramList = new ArrayList<>();
            for (String pKey : params.keySet()) {
                paramList.add(new BasicNameValuePair(pKey, params.get(pKey)));
            }
            try {
                url = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(paramList, "UTF-8"));
            } catch (IOException e) {
                logger.error("创建参数时发生错误", e);
            }
        }

        HttpGet request = new HttpGet(url);
        //logger.debug("url: {}", url);

        //设置headers
        if (headers != null && !headers.isEmpty()) {
            for (String hKey : headers.keySet()) {
                request.addHeader(hKey, headers.get(hKey));
            }
        }

        return this.executeRequestGzip(request);
    }

    /**
     * 使用缺省的配置发送POST请求
     *
     * @param url
     * @return
     */
    public HttpResult post(String url) {
        HttpPost post = new HttpPost(url);
        return this.executeRequest(post);
    }

    /**
     * 使用缺省的配置发送POST请求，以json发生提交数据
     *
     * @param url
     * @param params
     * @return
     */
    public <T> HttpResult postJson(String url, T params) {
        HttpPost post = new HttpPost(url);

        if (params != null) {
            try {
                post.setHeader("Content-Type", "application/json;charset=UTF-8");
                StringEntity stringEntity;
                if (params instanceof String) {
                    stringEntity = new StringEntity((String) params, "UTF-8");
                } else {
                    stringEntity = new StringEntity(objectMapper.writeValueAsString(params), "UTF-8");
                }
                stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, "UTF-8"));
                post.setEntity(stringEntity);
            } catch (Exception e) {
                logger.error("创建参数时发生错误", e);
            }
        }

        return this.executeRequest(post);
    }

    /**
     * 使用缺省的配置发送POST请求，以form方式提交数据
     *
     * @param url
     * @param form
     * @return
     */
    public HttpResult postForm(String url, Map<String, Object> form) {
        HttpPost post = new HttpPost(url);

        if (form != null && !form.isEmpty()) {
            try {
                // 创建参数队列
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for (String fKey : form.keySet()) {
                    params.add(new BasicNameValuePair(fKey, form.get(fKey).toString()));
                }

                //参数转码
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                post.setEntity(entity);
            } catch (Exception e) {
                logger.error("创建参数时发生错误", e);
            }
        }

        return this.executeRequest(post);
    }


    /**
     * 使用缺省的配置发送POST请求，获取压缩数据，以form方式提交数据
     *
     * @param url
     * @param form
     * @return
     */
    public HttpResult postFormGzip(String url, Map<String, Object> form) {
        HttpPost post = new HttpPost(url);

        if (form != null && !form.isEmpty()) {
            try {
                // 创建参数队列
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for (String fKey : form.keySet()) {
                    params.add(new BasicNameValuePair(fKey, form.get(fKey).toString()));
                }

                //参数转码
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                post.setEntity(entity);
            } catch (Exception e) {
                logger.error("创建参数时发生错误", e);
            }
        }

        return this.executeRequestGzip(post);
    }

    //upload(filePath-String)

    /**
     * post上传文件
     * @param url
     * @param filePath
     * @return
     */
    /*public HttpResult fileUpload(String url, String filePath){
		if(StringUtils.isBlank(filePath)){
			logger.error("错误：文件路径为空！");
			return null;
		}

		HttpPost post = new HttpPost(url);

		FileBody bin = new FileBody(new File(filePath));
		StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

		HttpEntity entity = MultipartEntityBuilder.create()
				.addPart("bin", bin)
				.addPart("comment", comment)
				.build();

		post.setEntity(entity);
		logger.debug("executing request: {}", post.getRequestLine());
		return this.executeRequest(post);
	}
*/

    /**
     * 从response中提取相关信息
     *
     * @param response
     * @return
     * @throws IOException
     */
    private HttpResult extractResultInfo(HttpResponse response) throws IOException {
        boolean isUseGzip = false;
        Header[] headers = response.getHeaders("Content-Encoding");
        for (Header header : headers) {
            if ("gzip".equals(header.getValue())) {
                isUseGzip = true;
            }
        }
        HttpEntity entity = response.getEntity();
        HttpResult result = new HttpResult();
        int statusCode = response.getStatusLine().getStatusCode();
        result.setStatus(statusCode);
        if (statusCode != HttpStatus.SC_OK) {
            logger.warn("Response not OK: statusCode={}", statusCode);
        }
        if (entity != null) {

            if (isUseGzip) {
                GZIPInputStream in = null;
                InputStreamReader isr = null;
                java.io.BufferedReader br = null;
                try {
                    in = new GZIPInputStream(entity.getContent());
                    isr = new InputStreamReader(in, "UTF-8");
                    br = new java.io.BufferedReader(isr);
                    StringBuffer sb = new StringBuffer();
                    String tempbf;
                    while ((tempbf = br.readLine()) != null) {
                        sb.append(tempbf);
                        sb.append("\r\n");
                    }
                    br.close();
                    isr.close();
                    in.close();

                    result.setPayload(sb.toString());
                }finally {
                    if(br != null){
                        br.close();
                    }
                    if(isr != null){
                        isr.close();
                    }
                    if(in != null){
                        in.close();
                    }
                }
            } else {
                result.setPayload(EntityUtils.toString(entity, "UTF-8")); //解决中文乱码问题
            }
        }
        return result;
    }

    /**
     * 从response中提取相关信息
     *
     * @param response
     * @return
     * @throws IOException
     */
    private HttpResult extractResultInfoGzip(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        HttpResult result = new HttpResult();
        int statusCode = response.getStatusLine().getStatusCode();
        result.setStatus(statusCode);
        if (statusCode != HttpStatus.SC_OK) {
            logger.warn("Response not OK: statusCode={}", statusCode);
        }
        if (entity != null) {

            GZIPInputStream in = null;
            InputStreamReader isr = null;
            java.io.BufferedReader br = null;
            try {
                in = new GZIPInputStream(entity.getContent());
                isr = new InputStreamReader(in, "UTF-8");
                br = new java.io.BufferedReader(isr);
                StringBuffer sb = new StringBuffer();
                String tempbf;
                while ((tempbf = br.readLine()) != null) {
                    sb.append(tempbf);
                    sb.append("\r\n");
                }
                br.close();
                isr.close();
                in.close();

                result.setPayload(sb.toString());
            }finally {
                if(br != null){
                    br.close();
                }
                if(isr != null){
                    isr.close();
                }
                if(in != null){
                    in.close();
                }
            }
        }
        return result;
    }

    /**
     * 执行请求
     *
     * @param request
     * @return
     */
    private HttpResult executeRequest(HttpUriRequest request) {
        HttpResult result = null;

        try {
            //开始发送请求
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                result = this.extractResultInfo(response);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
			/*try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("httpClient close时发生错误", e);
			}*/
        }

        return result;
    }

    /**
     * 执行请求
     *
     * @param request
     * @return
     */
    private HttpResult executeRequestGzip(HttpUriRequest request) {
        HttpResult result = null;

        try {
            //开始发送请求
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                result = this.extractResultInfoGzip(response);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
			/*try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("httpClient close时发生错误", e);
			}*/
        }

        return result;
    }
}
