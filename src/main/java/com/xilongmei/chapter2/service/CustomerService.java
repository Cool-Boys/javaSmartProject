package com.xilongmei.chapter2.service;

import com.xilongmei.chapter2.model.Customer;
import com.xilongmei.chapter2.util.PropsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CustomerService {



    public List<Customer> getCustomerList(String keyword) {

        return null;
    }

    public Customer getCustomer(long id) {
        return null;

    }

    public boolean createCustomer(Map<String, Object> fieldMap) {
        return false;
    }

    public boolean updateCustomer(long id, Map<String, Object> fieldMap) {
        return false;
    }

    public boolean deleteCustomer(long id) {
        return false;
    }
}
