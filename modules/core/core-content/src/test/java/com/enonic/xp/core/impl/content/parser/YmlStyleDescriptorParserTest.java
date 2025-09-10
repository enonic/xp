package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.style.ElementStyle;
import com.enonic.xp.style.GenericStyle;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YmlStyleDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/styles-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final StyleDescriptor.Builder builder = YmlStyleDescriptorParser.parse( yaml, currentApplication );

        builder.application( currentApplication );

        final StyleDescriptor descriptor = builder.build();

        assertEquals( currentApplication, descriptor.getApplicationKey() );
        assertEquals( "assets/styles.css", descriptor.getCssPath() );

        final List<ElementStyle> elements = descriptor.getElements();
        assertNotNull( elements );
        assertEquals( 3, elements.size() );

        final ElementStyle genericStyle = elements.getFirst();
        assertInstanceOf( GenericStyle.class, genericStyle );
        assertEquals( "warning", genericStyle.getName() );
        assertEquals( "Warning", genericStyle.getDisplayName() );
        assertEquals( "warning.displayName", genericStyle.getDisplayNameI18nKey() );

        final ElementStyle imageStyle_1 = elements.get( 1 );
        assertInstanceOf( ImageStyle.class, imageStyle_1 );
        assertEquals( "editor-width-auto", imageStyle_1.getName() );
        assertEquals( "Override ${width}", imageStyle_1.getDisplayName() );
        assertEquals( "editor-width-auto-text", imageStyle_1.getDisplayNameI18nKey() );

        assertInstanceOf( ImageStyle.class, elements.getLast() );

        final ImageStyle imageStyle_2 = (ImageStyle) elements.getLast();
        assertEquals( "editor-style-cinema", imageStyle_2.getName() );
        assertEquals( "Cinema", imageStyle_2.getDisplayName() );
        assertEquals( "editor-style-cinema-text", imageStyle_2.getDisplayNameI18nKey() );
        assertEquals( "21:9", imageStyle_2.getAspectRatio() );
        assertEquals( "pixelate(10)", imageStyle_2.getFilter() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlStyleDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
