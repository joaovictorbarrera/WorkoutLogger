package org.joaobarrera;

public class OperationResult<T> {
    private final boolean success;
    private final T data;
    private final String message;

    public OperationResult(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "Success: " + success +
                ", Data: " + data +
                ", Message: " + message;
    }
}
