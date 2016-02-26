package com.beetron.outmall.models;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/1/24.
 * Time: 09:44.
 */
public class ResultEntity<T> {

    private Boolean isSuccess;
    private T result;
    private String error;

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {

        this.error = error;
    }
}
