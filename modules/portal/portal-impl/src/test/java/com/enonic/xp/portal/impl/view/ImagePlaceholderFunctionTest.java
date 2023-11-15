package com.enonic.xp.portal.impl.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals( "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAC0lEQVR4nGNgwAQAABQAAX3+Hu4AAAAASUVORK5CYII=",
                      result );
    }

    @Test
    public void testExecute_widthRequired()
    {
        assertThrows(IllegalArgumentException.class, () -> execute( "imagePlaceholder", "height=2" ));
    }
}
