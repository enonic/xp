package com.enonic.wem.web.data.account;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.account.ChangePassword;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;

public class ChangePasswordRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final ChangePasswordRpcHandler handler = new ChangePasswordRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void testChangePasswordForIncorrectKey()
        throws Exception
    {
        mockCurrentContextHttpRequest();
        testSuccess( createParams( "12345", "t3stPa55word!" ), createResult( false, "Not a valid account key [12345]" ) );
    }

    @Test
    public void testChangePasswordForIncorrectValue()
        throws Exception
    {
        mockCurrentContextHttpRequest();
        testSuccess( createParams( "user:enonic:1", "Sh0rt!" ), createResult( false, "Password size must be between 8 and 64 symbols" ) );
    }

    @Test
    public void testChangePasswordForIncorrectType()
        throws Exception
    {
        mockCurrentContextHttpRequest();
        testSuccess( createParams( "group:enonic:1", "t3stPa55word!" ), createResult( false, "Passwords can be changed for users only" ) );
    }

    @Test
    public void testChangePassword()
        throws Exception
    {
        mockCurrentContextHttpRequest();
        Mockito.when( client.execute( Mockito.any( ChangePassword.class ) ) ).thenReturn( true );
        testSuccess( createParams( "user:enonic:1", "t3stPa55word!" ), createResult( true, null ) );
    }


    private ObjectNode createParams( String key, String password )
    {
        ObjectNode params = objectNode();
        params.put( "key", key );
        params.put( "password", password );
        return params;
    }

    private ObjectNode createResult( boolean success, String error )
    {
        ObjectNode result = objectNode();
        result.put( "success", success );
        if ( error != null )
        {
            result.put( "error", error );
        }
        return result;
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = new MockHttpServletRequest();
        final ServletRequestAttributes attrs = new ServletRequestAttributes( req );
        RequestContextHolder.setRequestAttributes( attrs );
    }

}
