package com.enonic.wem.web.rest2.resource.account;

import org.junit.Test;

import com.enonic.wem.api.account.AccountType;

import static org.junit.Assert.*;

public class RestUriBuilderTest
{

    @Test
    public void testGetImageUri()
    {
        final String key = "2BF83E35709BC83C6A80874D660788C65A32C93F";
        final String imageUserUri = AccountUriHelper.getImageUri( AccountType.USER, key );
        final String imageGroupUri = AccountUriHelper.getImageUri( AccountType.GROUP, key );
        final String imageRoleUri = AccountUriHelper.getImageUri( AccountType.ROLE, key );

        assertEquals( "account/user/2BF83E35709BC83C6A80874D660788C65A32C93F/photo", imageUserUri );
        assertNull( imageGroupUri );
        assertNull( imageRoleUri );
    }

    @Test
    public void testGetAccountInfoUri()
    {
        final String key = "2BF83E35709BC83C6A80874D660788C65A32C93F";
        final String userInfoUri = AccountUriHelper.getAccountInfoUri( AccountType.USER, key );
        final String groupInfoUri = AccountUriHelper.getAccountInfoUri( AccountType.GROUP, key );
        final String roleInfoUri = AccountUriHelper.getAccountInfoUri( AccountType.ROLE, key );

        assertEquals( "account/user/2BF83E35709BC83C6A80874D660788C65A32C93F", userInfoUri );
        assertEquals( "account/group/2BF83E35709BC83C6A80874D660788C65A32C93F", groupInfoUri );
        assertEquals( "account/role/2BF83E35709BC83C6A80874D660788C65A32C93F", roleInfoUri );
    }

    @Test
    public void testGetAccountInfoUriNull()
    {
        final String key = "2BF83E35709BC83C6A80874D660788C65A32C93F";
        final String accountInfoUri = AccountUriHelper.getAccountInfoUri( null, key );
        assertNull( accountInfoUri );
    }

}
