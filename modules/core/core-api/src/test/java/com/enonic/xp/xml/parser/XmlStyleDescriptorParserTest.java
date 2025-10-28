package com.enonic.xp.xml.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlStyleDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlStyleDescriptorParser parser;

    private StyleDescriptor.Builder builder;

    @BeforeEach
    void setup()
    {
        this.parser = new XmlStyleDescriptorParser();
        final ApplicationKey app = ApplicationKey.from( "myapplication" );
        this.parser.currentApplication( app );

        this.builder = StyleDescriptor.create();
        this.builder.application( app );
        this.parser.styleDescriptorBuilder( this.builder );
    }

    @Test
    void testParse()
        throws Exception
    {
        parse( this.parser, ".xml" );
        assertResult();
    }

    private void assertResult()
    {
        final StyleDescriptor result = this.builder.build();
        assertEquals( "myapplication", result.getApplicationKey().toString() );
        assertEquals( "assets/styles.css", result.getCssPath() );
        assertEquals( 3, result.getElements().size() );

        final ImageStyle element1 = result.getElements().get( 0 );
        final ImageStyle element2 = result.getElements().get( 1 );
        final ImageStyle element3 = result.getElements().get( 2 );

        assertEquals( "editor-align-justify", element1.getName() );
        assertEquals( "Justify", element1.getDisplayName() );
        assertEquals( "style.editor.align.justify", element1.getDisplayNameI18nKey() );

        assertEquals( "editor-width-auto", element2.getName() );
        assertEquals( "Override ${width}", element2.getDisplayName() );
        assertEquals( "editor-width-auto-text", element2.getDisplayNameI18nKey() );

        assertEquals( "editor-style-cinema", element3.getName() );
        assertEquals( "Cinema", element3.getDisplayName() );
        assertEquals( "editor-style-cinema-text", element3.getDisplayNameI18nKey() );
        assertEquals( "21:9", element3.getAspectRatio() );
        assertEquals( "pixelate(10)", element3.getFilter() );
    }
}
