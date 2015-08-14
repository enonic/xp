package com.enonic.xp.portal.impl.view;

import org.junit.Test;

import static org.junit.Assert.*;

public class PageUrlFunctionTest
    extends AbstractUrlViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        final PageUrlFunction function = new PageUrlFunction();
        function.setUrlService( createUrlService() );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "pageUrl", "_path=some/path", "a=1", "b=2" );
        assertEquals( "PageUrlParams{type=server, params={a=[1], b=[2]}, path=some/path}", result );
    }
}
