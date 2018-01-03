package com.enonic.xp.portal.impl.view;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationUrlFunctionTest
    extends AbstractUrlViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        final ApplicationUrlFunction function = new ApplicationUrlFunction();
        function.setUrlService( createUrlService() );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "applicationUrl", "_application=myapp", "_path=/subpath", "b=2" );
        assertEquals( "ApplicationUrlParams{type=server, params={b=[2]}, path=/subpath, application=myapp}", result );
    }
}
