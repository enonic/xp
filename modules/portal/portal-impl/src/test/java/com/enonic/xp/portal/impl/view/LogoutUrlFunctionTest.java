package com.enonic.xp.portal.impl.view;

import org.junit.Test;

import static org.junit.Assert.*;

public class LogoutUrlFunctionTest
    extends AbstractUrlViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        final LogoutUrlFunction function = new LogoutUrlFunction();
        function.setUrlService( createUrlService() );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "logoutUrl", "_userStore=system", "_redirect=/redirection/url" );
        assertEquals(
            "IdentityUrlParams{type=server, params={}, userStoreKey=system, idProviderFunction=logout, redirect=/redirection/url}",
            result );
    }
}
