package com.enonic.xp.xml.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.style.GenericStyle;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlStyleDescriptorParserTest
    extends XmlModelParserTest
{
//    private XmlStyleDescriptorParser parser;
//
//    private StyleDescriptor.Builder builder;
//
//    @BeforeEach
//    public void setup()
//    {
//        this.parser = new XmlStyleDescriptorParser();
//        final ApplicationKey app = ApplicationKey.from( "myapplication" );
//        this.parser.currentApplication( app );
//
//        this.builder = StyleDescriptor.create();
//        this.builder.application( app );
//        this.parser.styleDescriptorBuilder( this.builder );
//    }
//
//    @Test
//    public void testParse()
//        throws Exception
//    {
//        parse( this.parser, ".xml" );
//        assertResult();
//    }
//
//    private void assertResult()
//        throws Exception
//    {
//        final StyleDescriptor result = this.builder.build();
//        assertEquals( "myapplication", result.getApplicationKey().toString() );
//        assertEquals( "assets/styles.css", result.getCssPath() );
//        assertEquals( 4, result.getElements().size() );
//
//        final GenericStyle element0 = (GenericStyle) result.getElements().get( 0 );
//        final ImageStyle element1 = (ImageStyle) result.getElements().get( 1 );
//        final ImageStyle element2 = (ImageStyle) result.getElements().get( 2 );
//        final ImageStyle element3 = (ImageStyle) result.getElements().get( 3 );
//
//        assertEquals( "style", element0.getElement() );
//        assertEquals( "warning", element0.getName() );
//        assertEquals( "Warning", element0.getDisplayName() );
//        assertEquals( "warning.displayName", element0.getDisplayNameI18nKey() );
//
//        assertEquals( "image", element1.getElement() );
//        assertEquals( "editor-align-justify", element1.getName() );
//        assertEquals( "Justify", element1.getDisplayName() );
//        assertEquals( "style.editor.align.justify", element1.getDisplayNameI18nKey() );
//
//        assertEquals( "image", element2.getElement() );
//        assertEquals( "editor-width-auto", element2.getName() );
//        assertEquals( "Override ${width}", element2.getDisplayName() );
//        assertEquals( "editor-width-auto-text", element2.getDisplayNameI18nKey() );
//
//        assertEquals( "image", element3.getElement() );
//        assertEquals( "editor-style-cinema", element3.getName() );
//        assertEquals( "Cinema", element3.getDisplayName() );
//        assertEquals( "editor-style-cinema-text", element3.getDisplayNameI18nKey() );
//        assertEquals( "21:9", element3.getAspectRatio() );
//        assertEquals( "pixelate(10)", element3.getFilter() );
//    }
}
