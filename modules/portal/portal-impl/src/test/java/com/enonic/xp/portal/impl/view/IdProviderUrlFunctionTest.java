package com.enonic.xp.portal.impl.view;

import org.junit.Test;

import static org.junit.Assert.*;

public class IdProviderUrlFunctionTest
    extends AbstractUrlViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        final IdProviderUrlFunction function = new IdProviderUrlFunction();
        function.setUrlService( createUrlService() );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "idProviderUrl", "_userStore=system" );
        assertEquals( "IdentityUrlParams{type=server, params={}, userStoreKey=system}", result );
    }
}
