package com.acme.employee.server;

import com.acme.employee.domain.Employee;
import com.acme.employee.repository.EmployeeRepository;
import com.acme.employee.shared.EmployeeDTO;
import com.acme.employee.shared.EmployeeService;
import com.acme.employee.shared.ServiceException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GWT RPC servlet that is ALSO a Spring bean, so @Autowired works.
 * Registered in {@link com.acme.employee.config.GwtRpcConfig}.
 * (No @Transactional here: a CGLIB proxy of a servlet is fragile. The Spring Data
 * repository methods are already transactional.)
 */
@Component
public class EmployeeServiceImpl extends RemoteServiceServlet implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository repository;

    @Override
    public List<EmployeeDTO> getAll() throws ServiceException {
        try {
            return repository.findAllByOrderByIdAsc().stream().map(this::toDto).collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw dbError("load records", e);
        }
    }

    @Override
    public EmployeeDTO save(EmployeeDTO dto) throws ServiceException {
        if (dto == null) throw new ServiceException("No data received.");
        String name = dto.getName() == null ? "" : dto.getName().trim();
        if (name.isEmpty()) throw new ServiceException("Name is required.");
        if (name.length() > 100) throw new ServiceException("Name must be 100 characters or fewer.");
        if (Double.isNaN(dto.getSalaryPerHour()) || dto.getSalaryPerHour() < 0
                || dto.getSalaryPerHour() >= 100_000_000d) {
            throw new ServiceException("Salary per hour must be between 0 and 99,999,999.99.");
        }
        try {
            Long id = dto.getId() != null ? dto.getId() : repository.nextId().longValue();
            BigDecimal salary = BigDecimal.valueOf(dto.getSalaryPerHour()).setScale(2, RoundingMode.HALF_UP);
            return toDto(repository.save(new Employee(id, name, salary)));   // insert or update by ID
        } catch (DataAccessException e) {
            throw dbError("save the record", e);
        }
    }

    @Override
    public void delete(Long id) throws ServiceException {
        try {
            if (id == null || !repository.existsById(id)) throw new ServiceException("Record " + id + " no longer exists.");
            repository.deleteById(id);
        } catch (DataAccessException e) {
            throw dbError("delete the record", e);
        }
    }

    private EmployeeDTO toDto(Employee e) {
        return new EmployeeDTO(e.getId(), e.getName(), e.getSalaryPerHour().doubleValue());
    }

    private ServiceException dbError(String action, DataAccessException e) {
        log.error("Database failure while trying to {}", action, e);
        return new ServiceException("Could not " + action + ". Check the database connection and try again.");
    }

    /**
     * Inside a Spring Boot jar the GWT output lives on the classpath (classpath:/static),
     * not in the servlet context root, so the default policy lookup fails. Load it from the classpath.
     */
    @Override
    protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request,
                                                           String moduleBaseURL, String strongName) {
        try {
            String modulePath = new URL(moduleBaseURL).getPath();            // e.g. /employeeapp/
            String ctx = request.getContextPath();
            if (!modulePath.startsWith(ctx)) return null;
            String resource = "/static" + modulePath.substring(ctx.length()) + strongName + ".gwt.rpc";
            try (InputStream in = getClass().getResourceAsStream(resource)) {
                return in == null ? null : SerializationPolicyLoader.loadFromStream(in, null);
            }
        } catch (Exception e) {
            log.warn("Could not load GWT serialization policy {}", strongName, e);
            return null;
        }
    }
}
