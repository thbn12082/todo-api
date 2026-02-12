package com.binh.todo_api.http;

import java.util.Arrays;

public class ETags {
    public ETags() {
    }
    public static String todo(Long id, Long version ){
        return "\"todo-" + id + "-v" + version + "\"";
    }
    public static String stripW(String etag){
        if(etag.startsWith("W/")){
            return etag.substring(2).trim();
        }
        return etag;
    }
    public static boolean matches (String ifHeader, String curentEtag){
        if(ifHeader == null || ifHeader.isBlank()){
            return false;
        }
        ifHeader = ifHeader.trim();

        // dòng này có nghĩa là: client gửi lên giá trị * trong header If-Match -> họ không quan tâm phiên bản nào cả, chỉ cần tài nguyên còn tồn tại là được
        if("*".equals(ifHeader)) return true;

// tách các etag trong header If-Match ra, loại bỏ khoảng trắng và ký tự W/ nếu có, sau đó so sánh với etag hiện tại
        return Arrays.stream(ifHeader.split(",")).map(String::trim).map(ETags::stripW).anyMatch(x -> x.equals(curentEtag));

    }


}
