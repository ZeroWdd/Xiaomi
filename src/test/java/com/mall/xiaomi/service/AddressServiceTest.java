package com.mall.xiaomi.service;

import com.mall.xiaomi.exception.XmException;
import com.mall.xiaomi.mapper.AddressMapper;
import com.mall.xiaomi.pojo.Address;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddressServiceTest {

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private Address testAddress;
    private Address defaultAddress;

    @Before
    public void setUp() {
        testAddress = new Address();
        testAddress.setId(1);
        testAddress.setUserId(1);
        testAddress.setName("张三");
        testAddress.setPhone("13800138000");
        testAddress.setProvince("广东省");
        testAddress.setCity("深圳市");
        testAddress.setDistrict("南山区");
        testAddress.setDetail("科技园南路88号");
        testAddress.setIsDefault(0);

        defaultAddress = new Address();
        defaultAddress.setId(2);
        defaultAddress.setUserId(1);
        defaultAddress.setName("李四");
        defaultAddress.setPhone("13900139000");
        defaultAddress.setIsDefault(1);
    }

    @Test
    public void testGetAddressByUserId_Success() {
        List<Address> addresses = Arrays.asList(testAddress, defaultAddress);
        when(addressMapper.select(any(Address.class))).thenReturn(addresses);

        List<Address> result = addressService.getAddressByUserId("1");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(addressMapper, times(1)).select(any(Address.class));
    }

    @Test(expected = XmException.class)
    public void testGetAddressByUserId_NotFound() {
        when(addressMapper.select(any(Address.class))).thenReturn(Collections.emptyList());

        addressService.getAddressByUserId("1");
    }

    @Test
    public void testAddAddress_NonDefault() {
        when(addressMapper.insert(any(Address.class))).thenReturn(1);

        testAddress.setIsDefault(0);
        addressService.addAddress(testAddress);

        verify(addressMapper, times(1)).insert(eq(testAddress));
        verify(addressMapper, never()).selectOne(any(Address.class));
    }

    @Test
    public void testAddAddress_SetDefaultWhenNoExistingDefault() {
        when(addressMapper.selectOne(any(Address.class))).thenReturn(null);
        when(addressMapper.insert(any(Address.class))).thenReturn(1);

        testAddress.setIsDefault(1);
        addressService.addAddress(testAddress);

        verify(addressMapper, times(1)).selectOne(any(Address.class));
        verify(addressMapper, never()).updateByPrimaryKeySelective(any(Address.class));
        verify(addressMapper, times(1)).insert(eq(testAddress));
    }

    @Test
    public void testAddAddress_SetDefaultWhenExistingDefault() {
        when(addressMapper.selectOne(any(Address.class))).thenReturn(defaultAddress);
        when(addressMapper.insert(any(Address.class))).thenReturn(1);

        testAddress.setIsDefault(1);
        addressService.addAddress(testAddress);

        verify(addressMapper, times(1)).selectOne(any(Address.class));
        verify(addressMapper, times(1)).updateByPrimaryKeySelective(argThat(addr -> 
            addr.getIsDefault() == 0 && addr.getId().equals(2)
        ));
        verify(addressMapper, times(1)).insert(eq(testAddress));
    }

    @Test
    public void testAddAddress_NullIsDefaultDefaultsToZero() {
        when(addressMapper.insert(any(Address.class))).thenReturn(1);

        testAddress.setIsDefault(null);
        addressService.addAddress(testAddress);

        assertEquals(Integer.valueOf(0), testAddress.getIsDefault());
        verify(addressMapper, times(1)).insert(eq(testAddress));
    }

    @Test(expected = XmException.class)
    public void testAddAddress_FailedInsert() {
        when(addressMapper.insert(any(Address.class))).thenReturn(0);

        addressService.addAddress(testAddress);
    }

    @Test
    public void testUpdateAddress_NonDefault() {
        when(addressMapper.updateByPrimaryKeySelective(any(Address.class))).thenReturn(1);

        testAddress.setIsDefault(0);
        addressService.updateAddress(testAddress);

        verify(addressMapper, times(1)).updateByPrimaryKeySelective(eq(testAddress));
        verify(addressMapper, never()).selectOne(any(Address.class));
    }

    @Test
    public void testUpdateAddress_SetDefault() {
        when(addressMapper.selectOne(any(Address.class))).thenReturn(defaultAddress);
        when(addressMapper.updateByPrimaryKeySelective(any(Address.class))).thenReturn(1);

        testAddress.setIsDefault(1);
        addressService.updateAddress(testAddress);

        verify(addressMapper, times(1)).selectOne(any(Address.class));
        verify(addressMapper, times(1)).updateByPrimaryKeySelective(argThat(addr -> 
            addr.getIsDefault() == 0 && addr.getId().equals(2)
        ));
        verify(addressMapper, times(1)).updateByPrimaryKeySelective(eq(testAddress));
    }

    @Test
    public void testUpdateAddress_NullIsDefault() {
        when(addressMapper.updateByPrimaryKeySelective(any(Address.class))).thenReturn(1);

        testAddress.setIsDefault(null);
        addressService.updateAddress(testAddress);

        verify(addressMapper, times(1)).updateByPrimaryKeySelective(eq(testAddress));
        verify(addressMapper, never()).selectOne(any(Address.class));
    }

    @Test(expected = XmException.class)
    public void testUpdateAddress_FailedUpdate() {
        when(addressMapper.updateByPrimaryKeySelective(any(Address.class))).thenReturn(0);

        addressService.updateAddress(testAddress);
    }

    @Test
    public void testDeleteAddress_Success() {
        when(addressMapper.delete(any(Address.class))).thenReturn(1);

        addressService.deleteAddress("1", "1");

        verify(addressMapper, times(1)).delete(argThat(addr -> 
            addr.getId().equals(1) && addr.getUserId().equals(1)
        ));
    }

    @Test(expected = XmException.class)
    public void testDeleteAddress_FailedDelete() {
        when(addressMapper.delete(any(Address.class))).thenReturn(0);

        addressService.deleteAddress("1", "1");
    }

    @Test
    public void testGetById_Success() {
        when(addressMapper.selectByPrimaryKey(1)).thenReturn(testAddress);

        Address result = addressService.getById("1");

        assertNotNull(result);
        assertEquals(Integer.valueOf(1), result.getId());
        verify(addressMapper, times(1)).selectByPrimaryKey(1);
    }

    @Test
    public void testTransactionality_AllOperationsInTransaction() {
        when(addressMapper.selectOne(any(Address.class))).thenReturn(defaultAddress);
        when(addressMapper.updateByPrimaryKeySelective(any(Address.class))).thenReturn(1);
        when(addressMapper.insert(any(Address.class))).thenReturn(1);

        testAddress.setIsDefault(1);
        addressService.addAddress(testAddress);

        verify(addressMapper, times(1)).selectOne(any(Address.class));
        verify(addressMapper, times(1)).updateByPrimaryKeySelective(any(Address.class));
        verify(addressMapper, times(1)).insert(any(Address.class));
    }
}
