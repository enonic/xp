package com.enonic.xp.style;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StyleDescriptorTest
{

    @Test
    public void testCreate()
    {
        ImageStyle element = ImageStyle.create().
            name( "editor-style-cinema" ).
            displayName( "Cinema" ).
            displayNameI18nKey( "editor-style-cinema-text" ).
            aspectRatio( "21:9" ).
            build();

        StyleDescriptor styleDescriptor = StyleDescriptor.create().
            application( ApplicationKey.from( "myapp" ) ).
            cssPath( "assets/styles.css" ).
            addStyleElement( element ).
            build();

        assertEquals( ApplicationKey.from( "myapp" ), styleDescriptor.getApplicationKey() );
        assertEquals( "assets/styles.css", styleDescriptor.getCssPath() );
        assertEquals( 1, styleDescriptor.getElements().size() );
    }

    @Test
    public void testDuplicateStyles()
    {
        ImageStyle element = ImageStyle.create().
            name( "editor-style-cinema" ).
            displayName( "Cinema" ).
            displayNameI18nKey( "editor-style-cinema-text" ).
            aspectRatio( "21:9" ).
            build();

        ImageStyle element2 = ImageStyle.create().
            name( "editor-style-cinema" ).
            displayName( "Cinema" ).
            build();

        assertThrows(IllegalArgumentException.class, () ->
        StyleDescriptor.create().
            application( ApplicationKey.from( "myapp" ) ).
            cssPath( "assets/styles.css" ).
            addStyleElement( element ).
            addStyleElement( element2 ).
            build() );
    }

    @Test
    public void testToResourceKey()
    {
        final ResourceKey resourceKey = StyleDescriptor.toResourceKey( ApplicationKey.from( "myapp" ) );
        assertEquals( "myapp:/site/styles.xml", resourceKey.toString() );
    }

    @Test
    public void testEquals()
    {
        ImageStyle element = ImageStyle.create().
            name( "editor-style-cinema" ).
            displayName( "Cinema" ).
            displayNameI18nKey( "editor-style-cinema-text" ).
            filter( "pixelate(10)" ).
            build();

        StyleDescriptor styleDescriptor = StyleDescriptor.create().
            application( ApplicationKey.from( "myapp" ) ).
            cssPath( "assets/styles.css" ).
            addStyleElement( element ).
            build();

        ImageStyle element2 = ImageStyle.create().
            name( "editor-style-cinema" ).
            displayName( "Cinema" ).
            displayNameI18nKey( "editor-style-cinema-text" ).
            filter( "pixelate(10)" ).
            build();

        StyleDescriptor styleDescriptor2 = StyleDescriptor.create().
            application( ApplicationKey.from( "myapp" ) ).
            cssPath( "assets/styles.css" ).
            addStyleElement( element2 ).
            build();

        StyleDescriptor styleDescriptor3 = StyleDescriptor.create().
            application( ApplicationKey.from( "myapp" ) ).
            cssPath( "assets/styles.css" ).
            build();

        assertEquals( styleDescriptor, styleDescriptor2 );
        assertEquals( styleDescriptor.hashCode(), styleDescriptor2.hashCode() );

        assertNotEquals( styleDescriptor, styleDescriptor3 );
        assertNotEquals( styleDescriptor.hashCode(), styleDescriptor3.hashCode() );

        assertNotEquals( styleDescriptor, styleDescriptor2.toString() );

        assertEquals( styleDescriptor, styleDescriptor );
    }
}
