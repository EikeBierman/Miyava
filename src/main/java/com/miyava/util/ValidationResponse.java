package com.miyava.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class ValidationResponse {

    private Status status;

    @Autowired
    protected MessageUtils messageUtils;

    /**
     * The type of the message to be displayed. The type is used to show message in a different style.
     */
    public static enum Status {
        ERROR, FAIL, SUCCESS;
    }

    private List<ErrorMessage> errorMessageList;

    private Message message;

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus( Status status ) {
        this.status = status;
    }

    /**
     * @return the errorMessageList
     */
    public List<ErrorMessage> getErrorMessageList() {
        return errorMessageList;
    }

    /**
     * @param errorMessageList the errorMessageList to set
     */
    public void setErrorMessageList( List<ErrorMessage> errorMessageList ) {
        this.errorMessageList = errorMessageList;
    }

    /**
     * @return the message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage( Message message ) {
        this.message = message;
    }

}
