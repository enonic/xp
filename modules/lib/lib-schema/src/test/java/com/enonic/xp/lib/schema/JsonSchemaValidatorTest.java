package com.enonic.xp.lib.schema;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.content.ContentType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonSchemaValidatorTest
{
    @Test
    void testContentType()
        throws IOException
    {
        JsonSchemaRegistry schemaRegistry = new JsonSchemaRegistry();
        schemaRegistry.activate();

        final String textLineSchema =
            new String( JsonSchemaValidatorTest.class.getResourceAsStream( "/extenstions/textline.schema.json" ).readAllBytes(),
                        StandardCharsets.UTF_8 );

        schemaRegistry.registerInputType( textLineSchema );

        JsonSchemaServiceImpl schemaService = new JsonSchemaServiceImpl( schemaRegistry );
        schemaService.activate();

        final String yaml =
            new String( JsonSchemaValidatorTest.class.getResourceAsStream( "/descriptors/article-content-type.yml" ).readAllBytes(),
                        StandardCharsets.UTF_8 );

        assertTrue( schemaService.isContentTypeValid( yaml ) );

        final ObjectMapper MAPPER = new ObjectMapper( new YAMLFactory() );
        final Map<String, Object> contentTypeMap = MAPPER.readValue( yaml, new TypeReference<>()
        {
        } );

        final ContentType.Builder builder = ContentType.create();

        final YmlContentTypeParser parser = new YmlContentTypeParser();
        parser.currentApplication( ApplicationKey.from( "myapp" ) );
        parser.builder( builder );
        parser.parse( contentTypeMap );

        final ContentType contentType = builder.build();

        assertEquals( "article:", contentType.getName().toString() );
    }

    @Test
    void testValidateRadioButton()
    {
        JsonSchemaRegistry schemaRegistry = new JsonSchemaRegistry();
        schemaRegistry.activate();

        JsonSchemaServiceImpl schemaService = new JsonSchemaServiceImpl( schemaRegistry );
        schemaService.activate();

        assertTrue( schemaService.isSchemaValid( "https://xp.enonic.com/schemas/json/inputTypes/radiobutton.schema.json", """
            type: "RadioButton"
            name: "myRadioButton"
            label:
              text: "myRadioButton"
              i18n: "i18n.myRadioButton"
            occurrences:
              minimum: 1
              maximum: 1
            option:
            - value: "one"
              i18n:
                text: One
                i18n: i18n.one
            - value: "two"
              i18n:
                text: Two
                i18n: i18n.two
            default: "one"
              
            """ ) );
    }

    @Test
    void testValidateTag()
    {
        JsonSchemaRegistry schemaRegistry = new JsonSchemaRegistry();
        schemaRegistry.activate();

        JsonSchemaServiceImpl schemaService = new JsonSchemaServiceImpl( schemaRegistry );
        schemaService.activate();

        assertTrue( schemaService.isSchemaValid( "https://xp.enonic.com/schemas/json/inputTypes/tag.schema.json", """
            type: "Tag"
            name: "mytag"
            label:
              text: "My Tag"
            occurrences:
              minimum: 0
              maximum: 0
                
            """ ) );
    }

    @Test
    void testValidateTextLine()
    {
        JsonSchemaRegistry schemaRegistry = new JsonSchemaRegistry();
        schemaRegistry.activate();

        JsonSchemaServiceImpl schemaService = new JsonSchemaServiceImpl( schemaRegistry );
        schemaService.activate();

        assertTrue( schemaService.isSchemaValid( "https://xp.enonic.com/schemas/json/inputTypes/textline.schema.json", """
            type: "TextLine"
            name: "socialsecuritynumber"
            label:
              text: "My SSN"
            default: "000-00-0000"
            maxLength: 11
            regexp: \\b\\d{3}-\\d{2}-\\d{4}\\b  
            occurrences:
              minimum: 1
              maximum: 3 
              
            """ ) );
    }

    @Test
    void testValidateTime()
    {
        JsonSchemaRegistry schemaRegistry = new JsonSchemaRegistry();
        schemaRegistry.activate();

        JsonSchemaServiceImpl schemaService = new JsonSchemaServiceImpl( schemaRegistry );
        schemaService.activate();

        assertTrue( schemaService.isSchemaValid( "https://xp.enonic.com/schemas/json/inputTypes/time.schema.json", """
            type: "Time"
            name: "mytime"
            label:
              text: "My Time"
            default: "13:22"
                        
            """ ) );
    }

    @Test
    void testValidateLong()
    {
        JsonSchemaRegistry schemaRegistry = new JsonSchemaRegistry();
        schemaRegistry.activate();

        JsonSchemaServiceImpl schemaService = new JsonSchemaServiceImpl( schemaRegistry );
        schemaService.activate();

        assertTrue( schemaService.isSchemaValid( "https://xp.enonic.com/schemas/json/inputTypes/long.schema.json", """
            type: "Long"
            name: "degrees"
            label:
              text: "Degrees"
            default: 70
            min: 0
            max: 360
                        
            """ ) );
    }

    @Test
    void testValidateDouble()
    {
        JsonSchemaRegistry schemaRegistry = new JsonSchemaRegistry();
        schemaRegistry.activate();

        JsonSchemaServiceImpl schemaService = new JsonSchemaServiceImpl( schemaRegistry );
        schemaService.activate();

        assertTrue( schemaService.isSchemaValid( "https://xp.enonic.com/schemas/json/inputTypes/double.schema.json", """
            type: "Double"
            name: "mydouble"
            label:
              text: "My Double"
            default: 3.89
            min: 0
            max: 3.14159
                        
            """ ) );
    }
}
