package at.spc.bean;

/**
 * Created by Terry LIANG on 2017/9/20.
 */
public class GenernalRestResponse {
    private int statusCode;
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static GenernalRestResponse buildResponse(int statusCode, String message) {
        GenernalRestResponse genernalRestResponse = new GenernalRestResponse();
        genernalRestResponse.setStatusCode(statusCode);
        genernalRestResponse.setMessage(message);
        return genernalRestResponse;
    }
}
