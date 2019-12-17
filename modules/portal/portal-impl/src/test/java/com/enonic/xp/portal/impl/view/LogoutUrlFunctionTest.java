package com.enonic.xp.portal.impl.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        final Object result = execute( "logoutUrl", "_idProvider=system", "_redirect=/redirection/url" );
        assertEquals(
            "IdentityUrlParams{type=server, params={}, idProviderKey=system, idProviderFunction=logout, redirect=/redirection/url}",
            result );
    }
}
