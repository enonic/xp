package com.enonic.xp.core.impl.schema;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YmlTypeParserTest
{
    @Test
    void testParse()
        throws Exception
    {
        YmlTypeParser parser = new YmlTypeParser();

        String yaml = readAsString( "/descriptors/content-type.yml" );

        ContentType.Builder builder = parser.parse( yaml, ContentType.Builder.class );

        builder.name( "myapp:article" );

        final Instant now = Instant.now();
        builder.createdTime( now );

        final ContentType contentType = builder.build();

        assertEquals( ContentTypeName.from( "myapp:article" ), contentType.getName() );
        assertEquals( "Article", contentType.getDisplayName() );
        assertEquals( "i18n.article.displayName", contentType.getDisplayNameI18nKey() );
        assertEquals( "${expression}", contentType.getDisplayNameExpression() );
        assertNotNull( contentType.getForm() );
        assertEquals( now, contentType.getCreatedTime() );
    }

//    @Test
//    void testParseRadioButton()
//        throws Exception
//    {
//        final String yaml = readAsString( "/descriptors/radiobutton-type.yml" );
//
//        YmlTypeParser parser = new YmlTypeParser();
//        Input input = parser.parse( yaml, Input.class );
//
//        assertEquals( "RadioButton", input.getInputType().toString() );
//
//        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );
//
//        final Value defaultValue = inputType.createDefaultValue( input );
//        assertTrue( defaultValue.isString() );
//        assertEquals( "cookie", defaultValue.asString() );
//
//        final InputTypeProperty cookieOpt = input.getInputTypeConfig().getProperty( "cookie" );
//        assertNotNull( cookieOpt );
//        assertEquals( "Cookie", cookieOpt.getValue() );
//        assertEquals( "v1", cookieOpt.getAttribute( "attr1" ) );
//        assertEquals( "v2", cookieOpt.getAttribute( "attr2" ) );
//
//        final InputTypeProperty privacyOpt = input.getInputTypeConfig().getProperty( "privacy" );
//        assertNotNull( privacyOpt );
//        assertEquals( "Privacy", privacyOpt.getValue() );
//        assertTrue( privacyOpt.getAttributes().isEmpty() );
//    }
//
//    @Test
//    void parseDouble()
//        throws Exception
//    {
//        final String yaml = readAsString( "/descriptors/double-type.yml" );
//
//        YmlTypeParser parser = new YmlTypeParser();
//        Input input = parser.parse( yaml, Input.class );
//
//        assertEquals( "Double", input.getInputType().toString() );
//
//        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );
//
//        final Value defaultValue = inputType.createDefaultValue( input );
//        assertTrue( defaultValue.isNumericType() );
//        assertEquals( 250.0, defaultValue.asDouble() );
//
//        final InputTypeProperty minOpt = input.getInputTypeConfig().getProperty( "min" );
//        assertNotNull( minOpt );
//        assertEquals( 0, Double.parseDouble( minOpt.getValue() ) );
//        assertTrue( minOpt.getAttributes().isEmpty() );
//
//        final InputTypeProperty maxOpt = input.getInputTypeConfig().getProperty( "max" );
//        assertNotNull( maxOpt );
//        assertEquals( 255, Double.parseDouble( maxOpt.getValue() ) );
//        assertTrue( maxOpt.getAttributes().isEmpty() );
//    }
//
//    @Test
//    void parseContentSelector()
//        throws Exception
//    {
//        final String yaml = readAsString( "/descriptors/contentselector-type.yml" );
//
//        YmlTypeParser parser = new YmlTypeParser();
//        Input input = parser.parse( yaml, Input.class );
//
//        assertEquals( "ContentSelector", input.getInputType().toString() );
//        assertEquals( "searchResultPage", input.getName() );
//        assertEquals( "Search result page", input.getLabel() );
//
//        Occurrences occurrences = input.getOccurrences();
//        assertEquals( 1, occurrences.getMinimum() );
//        assertEquals( 1, occurrences.getMaximum() );
//
//        final InputTypeProperty configOpt = input.getInputTypeConfig().getProperty( "allow-content-type" );
//        assertNotNull( configOpt );
//        assertEquals( "myapp:landing-page", configOpt.getValue() );
//        assertTrue( configOpt.getAttributes().isEmpty() );
//    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlTypeParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
