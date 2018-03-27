package com.enonic.xp.portal.impl.view;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginUrlFunctionTest
    extends AbstractUrlViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        final LoginUrlFunction function = new LoginUrlFunction();
        function.setUrlService( createUrlService() );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "loginUrl", "_userStore=system", "_redirect=/redirection/url" );
        assertEquals( "IdentityUrlParams{type=server, params={}, userStoreKey=system, idProviderFunction=login, redirect=/redirection/url}",
                      result );
    }
}
