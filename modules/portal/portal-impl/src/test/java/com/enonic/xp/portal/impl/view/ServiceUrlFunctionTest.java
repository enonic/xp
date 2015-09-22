package com.enonic.xp.portal.impl.view;

import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceUrlFunctionTest
    extends AbstractUrlViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        final ServiceUrlFunction function = new ServiceUrlFunction();
        function.setUrlService( createUrlService() );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "serviceUrl", "_service=a", "b=2" );
        assertEquals( "ServiceUrlParams{type=server, params={b=[2]}, service=a}", result );
    }
}
