package com.enonic.xp.core.impl.image.effect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImageFiltersArgumentsTest
    extends BaseImageFilterTest
{
    @Test
    void blurRadiusWithinBounds()
    {
        assertNotNull( newFilters().blur( 0 ).apply( getOpaque() ) );
        assertNotNull( newFilters().blur( 100 ).apply( getOpaque() ) );
    }

    @Test
    void blurRadiusOutOfBounds()
    {
        assertThrows( IllegalArgumentException.class, () -> newFilters().blur( -1 ) );
        assertThrows( IllegalArgumentException.class, () -> newFilters().blur( 101 ) );
        assertThrows( IllegalArgumentException.class, () -> newFilters().blur( 5_000_000 ) );
    }

    @Test
    void tooManyArguments()
    {
        assertThrows( IllegalArgumentException.class, () -> newFilters().blur( 2, 1 ) );
        assertThrows( IllegalArgumentException.class, () -> newFilters().block( 2, 1 ) );
        assertThrows( IllegalArgumentException.class, () -> newFilters().border( 2, 0, 1 ) );
        assertThrows( IllegalArgumentException.class, () -> newFilters().rounded( 10, 0, 0, 1 ) );
        assertNotNull( newFilters().border( 2, 0 ).apply( getOpaque() ) );
        assertNotNull( newFilters().rounded( 10, 0, 0 ).apply( getOpaque() ) );
    }

    @Test
    void blockSizeOutOfBounds()
    {
        assertThrows( IllegalArgumentException.class, () -> newFilters().block( 0 ) );
        assertThrows( IllegalArgumentException.class, () -> newFilters().block( -5 ) );
        assertThrows( IllegalArgumentException.class, () -> newFilters().block( 1001 ) );
        assertNotNull( newFilters().block( 1000 ).apply( getOpaque() ) );
    }

    @Test
    void borderSizeOutOfBounds()
    {
        assertThrows( IllegalArgumentException.class, () -> newFilters().border( -1 ) );
        assertThrows( IllegalArgumentException.class, () -> newFilters().border( 1001 ) );
        assertNotNull( newFilters().border( 0 ).apply( getOpaque() ) );
    }

    @Test
    void roundedArgumentsOutOfBounds()
    {
        assertThrows( IllegalArgumentException.class, () -> newFilters().rounded( -1 ) );
        assertThrows( IllegalArgumentException.class, () -> newFilters().rounded( 1001 ) );
        assertThrows( IllegalArgumentException.class, () -> newFilters().rounded( 10, -1 ) );
        assertThrows( IllegalArgumentException.class, () -> newFilters().rounded( 10, 1001 ) );
        assertNotNull( newFilters().rounded( 0, 0 ).apply( getOpaque() ) );
    }
}
