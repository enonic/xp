package com.enonic.xp.portal.impl.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComponentUrlFunctionTest
    extends AbstractUrlViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        final ComponentUrlFunction function = new ComponentUrlFunction();
        function.setUrlService( createUrlService() );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "componentUrl", "_component=a" );
        assertEquals( "ComponentUrlParams{type=server, params={}, component=a}", result );
    }
}
