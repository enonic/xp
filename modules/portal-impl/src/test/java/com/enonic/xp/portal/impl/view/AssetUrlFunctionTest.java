package com.enonic.xp.portal.impl.view;

import org.junit.Test;

import static org.junit.Assert.*;

public class AssetUrlFunctionTest
    extends AbstractUrlViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        final AssetUrlFunction function = new AssetUrlFunction();
        function.setUrlService( createUrlService() );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "assetUrl", "_path=a", "b=2" );
        assertEquals( "AssetUrlParams{params={b=[2]}, path=a}", result );
    }
}
