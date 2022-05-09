package edu.nus.iss.sg.myrecipe.exceptions;

public class DeleteRecipeException extends RuntimeException {
    public DeleteRecipeException() {
        super();
    }

    public DeleteRecipeException(String msg) {
        super(msg);
    }
}
