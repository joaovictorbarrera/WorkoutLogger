package org.joaobarrera;

public class OperationResult<T> {
    private final boolean success;
    private final String message;
    private final T data;

    public OperationResult(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getMessage() { return message; }
}
