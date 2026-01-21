package com.binh.todo_api.error;

public class ConflicException extends RuntimeException{
    public ConflicException(String message){
        super(message);
    }
}
