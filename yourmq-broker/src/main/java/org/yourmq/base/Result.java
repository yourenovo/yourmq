//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;


import java.io.Serializable;

public class Result<T> implements Serializable {
    public static int SUCCEED_CODE = 200;
    public static int FAILURE_CODE = 400;
    private int code;
    private String description;
    private T data;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        if (description == null) {
            this.description = "";
        } else {
            this.description = description;
        }

    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

    public Result() {
        this.code = SUCCEED_CODE;
        this.description = "";
    }

    public Result(T data) {
        this.code = SUCCEED_CODE;
        this.description = "";
        this.data = data;
    }

    public Result(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Result(int code, String description, T data) {
        this.code = code;
        this.description = description;
        this.data = data;
    }

    @Override
    public String toString() {
        return "{code=" + this.code + ", description='" + this.description + '\'' + ", data=" + this.data + '}';
    }


    public static <T> Result<T> succeed() {
        return new Result(SUCCEED_CODE, "");
    }


    public static <T> Result<T> succeed(T data) {
        return new Result(data);
    }


    public static <T> Result<T> succeed(T data, String description) {
        return new Result(SUCCEED_CODE, description, data);
    }

    public static <T> Result<T> succeed(T data, int code) {
        return new Result(code, "", data);
    }


    public static <T> Result<T> failure() {
        return new Result(FAILURE_CODE, "");
    }

    public static <T> Result<T> failure(int code) {
        return failure(code, "");
    }

    public static <T> Result<T> failure(int code, String description) {
        return new Result(code, description);
    }

    public static <T> Result<T> failure(int code, String description, T data) {
        return new Result(code, description, data);
    }

    public static <T> Result<T> failure(String description) {
        return new Result(FAILURE_CODE, description);
    }
}
