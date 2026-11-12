package com.acme.employee.config;

import com.acme.employee.server.EmployeeServiceImpl;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GwtRpcConfig {

    /**
     * URL = {moduleBaseURL}{@RemoteServiceRelativePath} = /employeeapp/employeeService
     * The servlet instance is the Spring-managed bean, so injected fields are populated.
     */
    @Bean
    public ServletRegistrationBean<EmployeeServiceImpl> employeeServiceServlet(EmployeeServiceImpl servlet) {
        ServletRegistrationBean<EmployeeServiceImpl> reg =
                new ServletRegistrationBean<>(servlet, "/employeeapp/employeeService");
        reg.setName("employeeService");
        reg.setLoadOnStartup(1);
        return reg;
    }
}
