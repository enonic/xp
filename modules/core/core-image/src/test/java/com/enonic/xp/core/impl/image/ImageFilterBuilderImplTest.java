/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.image.parser.FilterSetExpr;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public final class ImageFilterBuilderImplTest
{
    private BufferedImage source;

    @BeforeEach
    void setUp()
        throws Exception
    {
        source = ImageIO.read( getClass().getResourceAsStream( "effect/source.jpg" ) );
    }

    @Test
    void testEmpty()
    {
        final BufferedImage source = mock( BufferedImage.class );

        final ImageFilterBuilderImpl imageFilterBuilder = new ImageFilterBuilderImpl();
        imageFilterBuilder.activate( mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );
        final ImageFunction imageFilter = imageFilterBuilder.build( FilterSetExpr.parse( "" ) );

        assertSame( source, imageFilter.apply( source ) );
    }

    @Test
    void testInvalidQuery()
    {
        final ImageFilterBuilderImpl imageFilterBuilder = new ImageFilterBuilderImpl();
        imageFilterBuilder.activate( mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        assertThrows( IllegalArgumentException.class, () -> imageFilterBuilder.build( FilterSetExpr.parse( "invalid1(1);invalid2(2)" ) ) );
    }

    @Test
    void testValidQuery()
    {
        final ImageFilterBuilderImpl imageFilterBuilder = new ImageFilterBuilderImpl();
        imageFilterBuilder.activate( mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        final ImageFunction imageFilter = imageFilterBuilder.build( FilterSetExpr.parse( "border(1);blur(2)" ) );

        assertNotSame( source, imageFilter.apply( source ) );
    }

    @Test
    void testTooManyFilters()
    {
        final ImageFilterBuilderImpl imageFilterBuilder = new ImageFilterBuilderImpl();
        imageFilterBuilder.activate( mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        final FilterSetExpr hugeFilter =
            FilterSetExpr.parse( Stream.generate( () -> "border(1)" ).limit( 26 ).collect( Collectors.joining( ";" ) ) );
        assertThrows( IllegalArgumentException.class, () -> imageFilterBuilder.build( hugeFilter ) );
    }

}
