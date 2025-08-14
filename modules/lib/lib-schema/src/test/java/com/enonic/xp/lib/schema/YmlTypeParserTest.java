package com.enonic.xp.lib.schema;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputType;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YmlTypeParserTest
{
    @Test
    void testParse()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/content-type.yml" );

        YmlTypeParser parser = new YmlTypeParser();
        ContentType contentType = parser.parseContentType( yaml );

        assertEquals( ContentTypeName.from( "myapp:article" ), contentType.getName() );
        assertEquals( ContentTypeName.from( "base:structured" ), contentType.getSuperType() );
        assertFalse( contentType.isAbstract() );
        assertFalse( contentType.isFinal() );
        assertTrue( contentType.allowChildContent() );
        assertFalse( contentType.isBuiltIn() );
        assertEquals( "Article heading", contentType.getDisplayNameLabel() );
        assertEquals( "article.title", contentType.getDisplayNameLabelI18nKey() );
        assertEquals( "${expression}", contentType.getDisplayNameExpression() );
    }

    @Test
    void testParseRadioButton()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/radiobutton-type.yml" );

        YmlTypeParser parser = new YmlTypeParser();
        Input input = parser.parse( yaml, Input.class );

        assertEquals( "RadioButton", input.getInputType().toString() );

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );

        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isString() );
        assertEquals( "cookie", defaultValue.asString() );

        final InputTypeProperty cookieOpt = input.getInputTypeConfig().getProperty( "cookie" );
        assertNotNull( cookieOpt );
        assertEquals( "Cookie", cookieOpt.getValue() );
        assertEquals( "v1", cookieOpt.getAttribute( "attr1" ) );
        assertEquals( "v2", cookieOpt.getAttribute( "attr2" ) );

        final InputTypeProperty privacyOpt = input.getInputTypeConfig().getProperty( "privacy" );
        assertNotNull( privacyOpt );
        assertEquals( "Privacy", privacyOpt.getValue() );
        assertTrue( privacyOpt.getAttributes().isEmpty() );
    }

    @Test
    void parseDouble()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/double-type.yml" );

        YmlTypeParser parser = new YmlTypeParser();
        Input input = parser.parse( yaml, Input.class );

        assertEquals( "Double", input.getInputType().toString() );

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );

        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isNumericType() );
        assertEquals( 250.0, defaultValue.asDouble() );

        final InputTypeProperty minOpt = input.getInputTypeConfig().getProperty( "min" );
        assertNotNull( minOpt );
        assertEquals( 0, Double.parseDouble( minOpt.getValue() ) );
        assertTrue( minOpt.getAttributes().isEmpty() );

        final InputTypeProperty maxOpt = input.getInputTypeConfig().getProperty( "max" );
        assertNotNull( maxOpt );
        assertEquals( 255, Double.parseDouble( maxOpt.getValue() ) );
        assertTrue( maxOpt.getAttributes().isEmpty() );
    }

    private String readAsString( final String name )
        throws IOException
    {
        return new String( YmlTypeParserTest.class.getResourceAsStream( name ).readAllBytes(), StandardCharsets.UTF_8 );
    }
}
