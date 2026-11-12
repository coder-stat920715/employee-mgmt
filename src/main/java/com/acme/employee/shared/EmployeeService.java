package com.acme.employee.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

@RemoteServiceRelativePath("employeeService")
public interface EmployeeService extends RemoteService {
    List<EmployeeDTO> getAll() throws ServiceException;
    EmployeeDTO save(EmployeeDTO employee) throws ServiceException;   // create or update
    void delete(Long id) throws ServiceException;
}
