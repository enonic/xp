package com.enonic.xp.portal.impl.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ImageUrlFunctionTest
    extends AbstractUrlViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        final ImageUrlFunction function = new ImageUrlFunction();
        function.setUrlService( createUrlService() );
        register( function );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "imageUrl", "_id=1" );
        assertEquals( "ImageUrlParams{type=server, params={}, id=1}", result );
    }
}
