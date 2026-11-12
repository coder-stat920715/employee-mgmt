package com.acme.employee.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface EmployeeServiceAsync {
    void getAll(AsyncCallback<List<EmployeeDTO>> callback);
    void save(EmployeeDTO employee, AsyncCallback<EmployeeDTO> callback);
    void delete(Long id, AsyncCallback<Void> callback);
}
