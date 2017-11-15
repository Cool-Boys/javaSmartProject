package com.xilongmei.chapter2.test;

import com.xilongmei.chapter2.model.Customer;
import com.xilongmei.chapter2.service.CustomerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerServiceTest {
    private final CustomerService customerService;

    public CustomerServiceTest() {
        customerService = new CustomerService();
    }

    @Before
    public void init() {

    }

    @Test
    public void getCustomerListTest() throws Exception {
        List<Customer> customerList = customerService.getCustomerList("");
        Assert.assertEquals(2, customerList.size());

    }

    @Test
    public void getCustomerTest() {

        long id = 1;
        Customer customer = customerService.getCustomer(id);
    }

    @Test
    public void createCustomerTest() {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("name", "customer100");
        fieldMap.put("contact", "john");
        fieldMap.put("telephone", "15247101310");
        boolean result = customerService.createCustomer(fieldMap);
        Assert.assertTrue(result);
    }

    @Test
    public void updateCustomerTest(){
        long id=1;
        Map<String,Object> fieldMap=new HashMap<String,Object>();
        fieldMap.put("contact","Eric");
        boolean result=customerService.updateCustomer(id,fieldMap);
        Assert.assertTrue(result);
    }
    @Test
    public void deleteCustomerTest(){
     long id=1;
     boolean result=customerService.deleteCustomer(id);
     Assert.assertTrue(result);
    }


}
