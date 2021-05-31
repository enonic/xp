package com.enonic.xp.portal.impl.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void testWithPath()
    {
        final Object result = execute( "imageUrl", "_path=a/b/c" );
        assertEquals( "ImageUrlParams{type=server, params={}, path=a/b/c}", result );
    }

    @Test
    public void testInvalidParams()
    {
        this.portalRequest.setContentPath( null );
        assertThrows( IllegalStateException.class, () -> execute( "imageUrl", "path=a/b/c" ) );
    }
}
