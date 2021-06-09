package com.enonic.xp.core.impl.image.effect;

import org.junit.jupiter.api.Test;

import com.enonic.xp.image.FocalPoint;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImageScalesTest
{
    @Test
    void block()
    {
        assertNotNull( new ImageScales( 10 ).block( FocalPoint.DEFAULT, 10, 5 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).block( FocalPoint.DEFAULT, 10, 5, 0 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).block( FocalPoint.DEFAULT, 11, 10 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).block( FocalPoint.DEFAULT, 10, 11 ) );
    }

    @Test
    void square()
    {
        assertNotNull( new ImageScales( 10 ).square( FocalPoint.DEFAULT, 10 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).square( FocalPoint.DEFAULT, 10, 10 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).square( FocalPoint.DEFAULT, 11 ) );
    }

    @Test
    void max()
    {
        assertNotNull( new ImageScales( 10 ).max( FocalPoint.DEFAULT, 10 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).max( FocalPoint.DEFAULT, 10, 10 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).max( FocalPoint.DEFAULT, 11 ) );
    }

    @Test
    void wide()
    {
        assertNotNull( new ImageScales( 10 ).wide( FocalPoint.DEFAULT, 10, 10 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).wide( FocalPoint.DEFAULT, 10, 5, 0 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).wide( FocalPoint.DEFAULT, 11, 10 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).wide( FocalPoint.DEFAULT, 10, 11 ) );
    }

    @Test
    void height()
    {
        assertNotNull( new ImageScales( 10 ).height( FocalPoint.DEFAULT, 10 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).height( FocalPoint.DEFAULT, 10, 10 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).height( FocalPoint.DEFAULT, 11 ) );
    }

    @Test
    void width()
    {
        assertNotNull( new ImageScales( 10 ).width( FocalPoint.DEFAULT, 10 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).width( FocalPoint.DEFAULT, 10, 10 ) );
        assertThrows( IllegalArgumentException.class, () -> new ImageScales( 10 ).width( FocalPoint.DEFAULT, 11 ) );
    }
}
