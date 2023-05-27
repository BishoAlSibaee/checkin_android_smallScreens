package com.syriasoft.hotelservices;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.gson.Gson;
import com.tuya.sdk.blelib.utils.StringUtils;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestSignUtils {

    // Access ID
    private static String accessId = "";
    // Access Secret
    private static String accessKey = "";
    // Tuya could endpoint
    private static String endpoint = "https://openapi.tuyacn.com";

    static {
        // Designated area domain name
        Constant.CONTAINER.put(Constant.ENDPOINT, endpoint);
        Constant.CONTAINER.put(Constant.ACCESS_ID, accessId);
        Constant.CONTAINER.put(Constant.ACCESS_KEY, accessKey);
    }
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String[] args) {

        String getTokenPath = "/v1.0/token?grant_type=1";
        Object result = RequestSignUtils.execute(getTokenPath, "GET", "", new HashMap<>());
        System.out.println(gson.toJson(result));
    }

    private static final MediaType CONTENT_TYPE = MediaType.parse("application/json");
    private static final String EMPTY_HASH = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    private static final String SING_HEADER_NAME = "Signature-Headers";
    private static final String NONE_STRING = "";

    private static final Gson gson = new Gson().newBuilder().create();


    /**
     * Used to obtain tokens, refresh tokens: no token request
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Object execute(String path, String method, String body, Map<String, String> customHeaders) {
        return RequestSignUtils.execute("", path, method, body, customHeaders);
    }

    /**
     * For business interface: carry Token request
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Object execute(String accessToken, String path, String method, String body, Map<String, String> customHeaders) {
        try {
            // Verify developer information
//            if (MapUtils.isEmpty(Constant.CONTAINER)) {
//                throw new TuyaCloudSDKException("Developer information is not initialized!");
//            }

            String url = Constant.CONTAINER.get(Constant.ENDPOINT) + path;

            Request.Builder request;
            if ("GET".equals(method)) {
                request = getRequest(url);
            } else if ("POST".equals(method)) {
                request = postRequest(url, body);
            } else if ("PUT".equals(method)) {
                request = putRequest(url, body);
            } else if ("DELETE".equals(method)) {
                request = deleteRequest(url, body);
            } else {
                throw new TuyaCloudSDKException("Method only support GET, POST, PUT, DELETE");
            }
            if (customHeaders.isEmpty()) {
                customHeaders = new HashMap<>();
            }
            Headers headers = getHeader(accessToken, request.build(), body, customHeaders);
            request.headers(headers);
            request.url(Constant.CONTAINER.get(Constant.ENDPOINT) + getPathAndSortParam(new URL(url)));
            Response response = doRequest(request.build());
            return gson.fromJson(response.body().string(), Object.class);
        } catch (Exception e) {
            throw new TuyaCloudSDKException(e.getMessage());
        }
    }

    /**
     * Generate header
     *
     * @param accessToken Do  need to carry token
     * @param headerMap   Custom header
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Headers getHeader(String accessToken, Request request, String body, Map<String, String> headerMap) throws Exception {
        Headers.Builder hb = new Headers.Builder();

        Map<String, String> flattenHeaders = flattenHeaders(headerMap);
        String t = flattenHeaders.get("t");
        if (StringUtils.isBlank(t)) {
            t = System.currentTimeMillis() + "";
        }

        hb.add("client_id", Constant.CONTAINER.get(Constant.ACCESS_ID));
        hb.add("t", t);
        hb.add("sign_method", "HMAC-SHA256");
        hb.add("lang", "zh");
        hb.add(SING_HEADER_NAME, flattenHeaders.getOrDefault(SING_HEADER_NAME, ""));
        String nonceStr = flattenHeaders.getOrDefault(Constant.NONCE_HEADER_NAME, "");
        hb.add(Constant.NONCE_HEADER_NAME, flattenHeaders.getOrDefault(Constant.NONCE_HEADER_NAME, ""));
        String stringToSign = stringToSign(request, body, flattenHeaders);
        if (StringUtils.isNotBlank(accessToken)) {
            hb.add("access_token", accessToken);
            hb.add("sign", sign(Constant.CONTAINER.get(Constant.ACCESS_ID), Constant.CONTAINER.get(Constant.ACCESS_KEY), t, accessToken, nonceStr, stringToSign));
        } else {
            hb.add("sign", sign(Constant.CONTAINER.get(Constant.ACCESS_ID), Constant.CONTAINER.get(Constant.ACCESS_KEY), t, nonceStr, stringToSign));
        }
        return hb.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getPathAndSortParam(URL url) {
        try {
            // supported the query contains zh-Han char
            String query = URLDecoder.decode(url.getQuery(), "UTF-8");
            String path = url.getPath();
            if (StringUtils.isBlank(query)) {
                return path;
            }
            Map<String, String> kvMap = new TreeMap<>();
            String[] kvs = query.split("\\&");
            for (String kv : kvs) {
                String[] kvArr = kv.split("=");
                if (kvArr.length > 1) {
                    kvMap.put(kvArr[0], kvArr[1]);
                } else {
                    kvMap.put(kvArr[0], "");
                }
            }
            return path + "?" + kvMap.entrySet().stream().map(it -> it.getKey() + "=" + it.getValue())
                    .collect(Collectors.joining("&"));
        } catch (Exception e) {
            return url.getPath();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String stringToSign(Request request, String body, Map<String, String> headers) throws Exception {
        List<String> lines = new ArrayList<>(16);
        lines.add(request.method().toUpperCase());
        String bodyHash = EMPTY_HASH;
        if (request.body() != null && request.body().contentLength() > 0) {
            bodyHash = Sha256Util.encryption(body);
        }
        String signHeaders = headers.get(SING_HEADER_NAME);
        String headerLine = "";
        if (signHeaders != null) {
            String[] sighHeaderNames = signHeaders.split("\\s*:\\s*");
            headerLine = Arrays.stream(sighHeaderNames).map(String::trim)
                    .filter(it -> it.length() > 0)
                    .map(it -> it + ":" + headers.get(it))
                    .collect(Collectors.joining("\n"));
        }
        lines.add(bodyHash);
        lines.add(headerLine);
        String paramSortedPath = getPathAndSortParam(request.url());
        lines.add(paramSortedPath);
        return String.join("\n", lines);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String getPathAndSortParam(HttpUrl url) {
        try {
            // supported the query contains zh-Han char
            String query = URLDecoder.decode(url.uri().getQuery(), "UTF-8");
            String path = url.uri().getPath();
            if (StringUtils.isBlank(query)) {
                return path;
            }
            Map<String, String> kvMap = new TreeMap<>();
            String[] kvs = query.split("\\&");
            for (String kv : kvs) {
                String[] kvArr = kv.split("=");
                if (kvArr.length > 1) {
                    kvMap.put(kvArr[0], kvArr[1]);
                } else {
                    kvMap.put(kvArr[0], "");
                }
            }
            return path + "?" + kvMap.entrySet().stream().map(it -> it.getKey() + "=" + it.getValue())
                    .collect(Collectors.joining("&"));
        } catch (Exception e) {
            return url.uri().getPath();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Map<String, String> flattenHeaders(Map<String, String> headers) {
        Map<String, String> newHeaders = new HashMap<>();
        headers.forEach((name, values) -> {
            if (values == null || values.isEmpty()) {
                newHeaders.put(name, "");
            } else {
                newHeaders.put(name, values);
            }
        });
        return newHeaders;
    }

    /**
     * Calculate sign
     */
    private static String sign(String accessId, String secret, String t, String accessToken, String nonce, String stringToSign) {
        StringBuilder sb = new StringBuilder();
        sb.append(accessId);
        if (StringUtils.isNotBlank(accessToken)) {
            sb.append(accessToken);
        }
        sb.append(t);
        if (StringUtils.isNotBlank(nonce)) {
            sb.append(nonce);
        }
        sb.append(stringToSign);
        return Sha256Util.sha256HMAC(sb.toString(), secret);
    }

    private static String sign(String accessId, String secret, String t, String nonce, String stringToSign) {
        return sign(accessId, secret, t, NONE_STRING, nonce, stringToSign);
    }

    /**
     * Handle get request
     */
    public static Request.Builder getRequest(String url) {
        Request.Builder request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .get();
        } catch (IllegalArgumentException e) {
            throw new TuyaCloudSDKException(e.getMessage());
        }
        return request;
    }

    /**
     * Handle post request
     */
    public static Request.Builder postRequest(String url, String body) {
        Request.Builder request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(CONTENT_TYPE, body));
        } catch (IllegalArgumentException e) {
            throw new TuyaCloudSDKException(e.getMessage());
        }

        return request;
    }

    /**
     * Handle put request
     */
    public static Request.Builder putRequest(String url, String body) {
        Request.Builder request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .put(RequestBody.create(CONTENT_TYPE, body));
        } catch (IllegalArgumentException e) {
            throw new TuyaCloudSDKException(e.getMessage());
        }
        return request;
    }


    /**
     * Handle delete request
     */
    public static Request.Builder deleteRequest(String url, String body) {
        Request.Builder request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .delete(RequestBody.create(CONTENT_TYPE, body));
        } catch (IllegalArgumentException e) {
            throw new TuyaCloudSDKException(e.getMessage());
        }
        return request;
    }

    /**
     * Execute request
     */
    public static Response doRequest(Request request) {
        Response response;
        try {
            response = getHttpClient().newCall(request).execute();
        } catch (IOException e) {
            throw new TuyaCloudSDKException(e.getMessage());
        }
        return response;
    }

    // Read timeout time (seconds)
    private static final int readTimeout = 30;
    // Write timeout time (seconds)
    private static final int writeTimeout = 30;
    //Connection timeout (seconds)
    private static final int connTimeout = 30;
    // Number of retries
    private static final int maxRetry = 3;

    // Get http client
    private static OkHttpClient getHttpClient() {
        OkHttpClient client = new OkHttpClient();
        //client.setConnectTimeout(connTimeout, TimeUnit.SECONDS);
        //client.setReadTimeout(readTimeout, TimeUnit.SECONDS);
        //client.setWriteTimeout(writeTimeout, TimeUnit.SECONDS);

        return client;
    }

    static class Constant {
        /**
         * Store developer information container
         */
        public static final Map<String, String> CONTAINER = new ConcurrentHashMap<String, String>();
        /**
         * Developer account, used as a key in the container
         */
        public static final String ACCESS_ID = "accessId";
        /**
         * Developer key, used as a key in the container
         */
        public static final String ACCESS_KEY = "accessKey";
        public static final String ENDPOINT = "endpoint";
        public static final String NONCE_HEADER_NAME = "nonce";
    }

    static class Sha256Util {

        public static String encryption(String str) throws Exception {
            return encryption(str.getBytes(StandardCharsets.UTF_8));
        }

        public static String encryption(byte[] buf) throws Exception {
            MessageDigest messageDigest;
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(buf);
            return byte2Hex(messageDigest.digest());
        }

        private static String byte2Hex(byte[] bytes) {
            StringBuilder stringBuffer = new StringBuilder();
            String temp;
            for (byte aByte : bytes) {
                temp = Integer.toHexString(aByte & 0xFF);
                if (temp.length() == 1) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(temp);
            }
            return stringBuffer.toString();
        }

        public static String xxxsha256HMAC(String content, String secret) {
            Mac sha256HMAC = null;
            try {
                sha256HMAC = Mac.getInstance("HmacSHA256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            try {
                sha256HMAC.init(secretKey);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            byte[] digest = sha256HMAC.doFinal(content.getBytes(StandardCharsets.UTF_8));
            String res = null ;
            if (digest != null ) {
                res = printHexBinary(digest);
            }
            return res ;
        }

        public static String sha256HMAC(String content, String secret) {
            Mac sha256HMAC = null;
            try {
                sha256HMAC = Mac.getInstance("HmacSHA256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            try {
                sha256HMAC.init(secretKey);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            byte[] digest = sha256HMAC.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return  printHexBinary(digest).toUpperCase();
            //return new HexBinaryAdapter().marshal(digest).toUpperCase();
        }

        public static String printHexBinary(byte[] data) {
            StringBuilder r = new StringBuilder(data.length * 2);
            for (byte b : data) {
                r.append(hexCode[(b >> 4) & 0xF]);
                r.append(hexCode[(b & 0xF)]);
            }
            return r.toString();
        }
    }


    static class TuyaCloudSDKException extends RuntimeException {

        private Integer code;

        public TuyaCloudSDKException(String message) {
            super(message);
        }

        public TuyaCloudSDKException(Integer code, String message) {
            super(message);
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        @Override
        public String toString() {
            if (code != null) {
                return "TuyaCloudSDKException: " +
                        "[" + code + "] " + getMessage();
            }

            return "TuyaCloudSDKException: " + getMessage();
        }
    }
}
