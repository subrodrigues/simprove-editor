/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:25
 */

package base;

public class MessageModel {
    String type;
    String code;
    String field;
    String message;

    public MessageModel(String type, String code, String field, String message) {
        this.type = type;
        this.code = code;
        this.field = field;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

}
