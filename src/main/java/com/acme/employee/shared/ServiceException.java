package com.acme.employee.shared;

import java.io.Serializable;

/** Business/validation error that is safe to show to the user. */
public class ServiceException extends Exception implements Serializable {
    public ServiceException() { }
    public ServiceException(String message) { super(message); }
}
