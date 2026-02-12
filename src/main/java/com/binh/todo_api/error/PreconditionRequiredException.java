package com.binh.todo_api.error;

public class PreconditionRequiredException extends RuntimeException{
    public PreconditionRequiredException(String message) { super(message); }
}
