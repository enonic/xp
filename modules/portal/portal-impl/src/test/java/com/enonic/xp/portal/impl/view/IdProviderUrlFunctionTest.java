package com.enonic.xp.portal.impl.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        final Object result = execute( "idProviderUrl", "_idProvider=system" );
        assertEquals( "IdentityUrlParams{type=server, params={}, idProviderKey=system}", result );
    }
}
