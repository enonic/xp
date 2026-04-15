package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.Style;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        final List<Style> elements = descriptor.getElements();
        assertNotNull( elements );
        assertEquals( 2, elements.size() );

        final Style imageStyle_1 = elements.getFirst();
        assertInstanceOf( Style.class, imageStyle_1 );

        assertEquals( "editor-width-auto", imageStyle_1.getName() );
        assertEquals( "Override ${width}", imageStyle_1.getLabel() );
        assertEquals( "editor-width-auto-text", imageStyle_1.getLabelI18nKey() );

        assertInstanceOf( Style.class, elements.getLast() );

        final ImageStyle imageStyle_2 = (ImageStyle) elements.getLast();
        assertEquals( "editor-style-cinema", imageStyle_2.getName() );
        assertEquals( "Cinema", imageStyle_2.getLabel() );
        assertEquals( "editor-style-cinema-text", imageStyle_2.getLabelI18nKey() );
        assertEquals( "21:9", imageStyle_2.getAspectRatio() );
        assertEquals( "pixelate(10)", imageStyle_2.getFilter() );

        final GenericValue editor = imageStyle_2.getEditor();
        assertNotNull( editor );

        final String css = editor.property( "css" ).asString();
        assertTrue( css.contains(".my-selector") );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlStyleDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
