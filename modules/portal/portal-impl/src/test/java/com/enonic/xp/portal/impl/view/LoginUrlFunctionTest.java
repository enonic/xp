package com.enonic.xp.portal.impl.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        final Object result = execute( "loginUrl", "_idProvider=system", "_redirect=/redirection/url" );
        assertEquals(
            "IdentityUrlParams{type=server, params={}, idProviderKey=system, idProviderFunction=login, redirect=/redirection/url}",
            result );
    }
}
