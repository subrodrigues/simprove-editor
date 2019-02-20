/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:24
 */

package base;

import java.util.List;

public class ResponseError {

    private List<MessageModel> messages;
    private ErrorType errorType;

    public ResponseError(ErrorType network) {
        this.errorType = network;
    }

    public ResponseError() {
        this(ErrorType.GENERIC);
    }

    public List<MessageModel> getMessages() {
        return messages;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "ResponseError{" +
                "messages=" + messages +
                ", errorType=" + errorType +
                '}';
    }

    /**
     * Returns true if there is no error.
     *
     * @return true if there is no error, false otherwise.
     */
    public boolean isSuccess() {
        return errorType == ErrorType.NONE;
    }

    /**
     * Returns true if the error is a network error.
     *
     * @return true if the error is a network error, false otherwise.
     */
    public boolean isNetworkError() {
        return errorType == ErrorType.NETWORK;
    }

    /**
     * Returns true if the error is a generic error.
     *
     * @return true if the error is a generic error, false otherwise.
     */
    public boolean isGenericError() {
        return errorType == ErrorType.GENERIC;
    }

    /**
     * Returns true if the error is a client side error.
     *
     * @return true if the error is a client side error, false otherwise.
     */
    public boolean isClientSideError() {
        return errorType == ErrorType.CLIENT_SIDE;
    }

    /**
     * Returns true if the error is a server side error.
     *
     * @return true if the error is a server side error, false otherwise.
     */
    public boolean isServerSideError() {
        return errorType == ErrorType.SERVER_SIDE;
    }

    /**
     * Returns true if the error is a message from the server.
     *
     * @return true if the error is a message from the server, false otherwise.
     */
    public boolean isCustomError() {
        return errorType == ErrorType.CUSTOM;
    }

    /**
     * Returns true if the error results from a canceled request.
     *
     * @return true if the error is a canceled request, false otherwise.
     */
    public boolean isCanceledError() {
        return errorType == ErrorType.CANCELED;
    }

    /**
     * Returns true if the body has no content
     *
     * @return true if the body has no content, false otherwise.
     */
    public boolean isContentEmpty() { return errorType == ErrorType.EMPTY; }

}
