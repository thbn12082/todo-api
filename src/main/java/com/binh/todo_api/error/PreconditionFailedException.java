package com.binh.todo_api.error;

public class PreconditionFailedException  extends RuntimeException{
    public PreconditionFailedException(String message) { super(message); }
}
