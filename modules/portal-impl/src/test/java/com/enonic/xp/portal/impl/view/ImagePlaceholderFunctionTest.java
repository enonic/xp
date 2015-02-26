package com.enonic.xp.portal.impl.view;

import org.junit.Test;

import static org.junit.Assert.*;

public class ImagePlaceholderFunctionTest
    extends AbstractViewFunctionTest
{
    @Override
    protected void setupFunction()
        throws Exception
    {
        register( new ImagePlaceholderFunction() );
    }

    @Test
    public void testExecute()
    {
        final Object result = execute( "imagePlaceholder", "width=2", "height=2" );
        assertEquals( "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAC0lEQVR42mNgQAcAABIAAeRVjecAAAAASUVORK5CYII=",
                      result );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecute_widthRequired()
    {
        execute( "imagePlaceholder", "height=2" );
    }
}
