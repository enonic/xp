package com.enonic.xp.style;

import org.junit.Test;

import static org.junit.Assert.*;

public class StyleImageTest
{
    @Test
    public void testCreate()
    {
        ImageStyle styleImage = ImageStyle.create().
            name( "editor-style-cinema" ).
            displayName( "Cinema" ).
            displayNameI18nKey( "editor-style-cinema-text" ).
            aspectRatio( "21:9" ).
            filter( "pixelate(10)" ).
            build();

        assertEquals( "image", styleImage.getElement() );
        assertEquals( "editor-style-cinema", styleImage.getName() );
        assertEquals( "Cinema", styleImage.getDisplayName() );
        assertEquals( "editor-style-cinema-text", styleImage.getDisplayNameI18nKey() );
        assertEquals( "21:9", styleImage.getAspectRatio() );
        assertEquals( "pixelate(10)", styleImage.getFilter() );
    }

    @Test
    public void testEquals()
    {
        ImageStyle styleImage = ImageStyle.create().
            name( "editor-style-cinema" ).
            displayName( "Cinema" ).
            displayNameI18nKey( "editor-style-cinema-text" ).
            aspectRatio( "21:9" ).
            filter( "pixelate(10)" ).
            build();

        ImageStyle styleImage1 = ImageStyle.create().
            name( "editor-style-cinema" ).
            displayName( "Cinema" ).
            displayNameI18nKey( "editor-style-cinema-text" ).
            aspectRatio( "21:9" ).
            filter( "pixelate(10)" ).
            build();

        ImageStyle styleImage2 = ImageStyle.create().
            name( "editor-style-cinema" ).
            displayName( "Cinema" ).
            displayNameI18nKey( "editor-style-cinema-text" ).
            build();

        assertEquals( styleImage, styleImage1 );
        assertEquals( styleImage.hashCode(), styleImage1.hashCode() );

        assertNotEquals( styleImage, styleImage2 );
        assertNotEquals( styleImage.hashCode(), styleImage2.hashCode() );

        assertNotEquals( styleImage, styleImage1.toString() );

        assertEquals( styleImage, styleImage );
    }
}