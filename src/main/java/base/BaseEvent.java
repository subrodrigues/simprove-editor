/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:29
 */

package base;

public abstract class BaseEvent {

    private ResponseError mError;
    private boolean mIsCached = false;

    public BaseEvent() {
        this(new ResponseError(ErrorType.NONE));
    }

    public BaseEvent(boolean isCached) {
        this();
        mIsCached = isCached;
    }

    public BaseEvent(ErrorType type) {
        this(new ResponseError(type));
    }

    public BaseEvent(ResponseError error) {
        this.mError = error;
    }

    public boolean isCached() {
        return mIsCached;
    }

    /**
     * Returns the error.
     *
     * @return the error.
     */
    public ResponseError getError() {
        return mError;
    }

    /**
     * Returns the error message.
     *
     * @return the error message.
     */
    public String getErrorMessage() {
        return getResponseErrorMessage(mError);
    }

    /**
     * Returns true if there is no error.
     *
     * @return true if there is no error, false otherwise.
     */
    public boolean isSuccess() {
        return mError.isSuccess();
    }

    /**
     * Returns true if the error is a network error.
     *
     * @return true if the error is a network error, false otherwise.
     */
    public boolean isNetworkError() {
        return mError.isNetworkError();
    }

    /**
     * Returns true if the error is a generic error.
     *
     * @return true if the error is a generic error, false otherwise.
     */
    public boolean isGenericError() {
        return mError.isGenericError();
    }


    /**
     * Returns true if the error is a client side error.
     *
     * @return true if the error is a client side error, false otherwise.
     */
    public boolean isClientSideError() {
        return mError.isGenericError();
    }

    /**
     * Returns true if the error is a server side error.
     *
     * @return true if the error is a server side error, false otherwise.
     */
    public boolean isServerSideError() {
        return mError.isServerSideError();
    }

    /**
     * Returns true if the error is a message from the server.
     *
     * @return true if the error is a message from the server, false otherwise.
     */
    public boolean isCustomError() {
        return mError.isCustomError();
    }

    /**
     * Returns true if the error results from a canceled request.
     *
     * @return true if the error is a canceled request, false otherwise.
     */
    public boolean isCanceledError() {
        return mError.isCanceledError();
    }

    /**
     * Returns true if the body has no content
     *
     * @return true if the body has no content, false otherwise.
     */
    public boolean isContentEmpty() { return mError.isContentEmpty(); }

    private static String getResponseErrorMessage(ResponseError error) {
        String errorMessage = null;
        if(error.getMessages() != null && error.getMessages().size() > 0) {
            errorMessage = error.getMessages().get(0).getMessage();
        }

        if(errorMessage == null || errorMessage.isEmpty()){
            errorMessage = "Unknown error."; // TODO: deal with this
        }

        return errorMessage;
    }
}
