package com.enonic.xp.core.impl.schema;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.mapper.InputYml;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YmlTypeParserTest
{
    private final YmlParserBase parser = new YmlParserBase();

    private static final ApplicationKey CURRENT_APPLICATION = ApplicationKey.from( "myapp" );

    private JsonSchemaServiceImpl validator;

    @BeforeEach
    void setUp()
    {
        validator = new JsonSchemaServiceImpl( null ); // TODO
        validator.activate();
    }

    @Test
    void testParseRadioButton()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/radiobutton-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "RadioButton", input.getInputType().toString() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        final Optional<GenericValue> options = inputTypeConfig.optional( "options" );
        assertTrue( options.isPresent() );
        assertEquals( 2, options.get().asList().size() );

        assertEquals( "cookie", inputTypeConfig.property( "default" ).asString() );
    }

    @Test
    void testParseTextLine()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/textline-type.yml" );

        assertDoesNotThrow( () -> validator.validate( "https://json-schema.enonic.com/8.0.0/textline.schema.json", yaml ) );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "TextLine", input.getInputType().toString() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertEquals( 11, inputTypeConfig.optional( "maxLength" ).map( GenericValue::asInteger ).orElse( null ) );

        assertEquals( "\\\\b\\\\d{3}-\\\\d{2}-\\\\d{4}\\\\b",
                      inputTypeConfig.optional( "regexp" ).map( GenericValue::asString ).orElse( null ) );
    }


    @Test
    void testParseDouble()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/double-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "Double", input.getInputType().toString() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertEquals( 0, inputTypeConfig.property( "min" ).asDouble() );
        assertEquals( 255, inputTypeConfig.property( "max" ).asDouble() );
        assertEquals( 250.0, inputTypeConfig.property( "default" ).asDouble() );
    }

    @Test
    void testParseContentSelector()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/contentselector-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "ContentSelector", input.getInputType().toString() );
        assertEquals( "searchResultPage", input.getName() );
        assertEquals( "Search result page", input.getLabel() );

        Occurrences occurrences = input.getOccurrences();
        assertEquals( 1, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "allowContentType" ).isPresent() );
        assertTrue( inputTypeConfig.optional( "allowPath" ).isPresent() );
        assertTrue( inputTypeConfig.optional( "hideToggleIcon" ).map( GenericValue::asBoolean ).orElse( false ) );
        assertTrue( inputTypeConfig.optional( "treeMode" ).map( GenericValue::asBoolean ).orElse( false ) );
    }

    @Test
    void testParseCustomSelector()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/customselector-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( InputTypeName.CUSTOM_SELECTOR, input.getInputType() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "service" ).isPresent() );
        assertTrue( inputTypeConfig.optional( "params" ).isPresent() );
    }

    @Test
    void testParseHtmlArea()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/htmlarea-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "HtmlArea", input.getInputType().toString() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "exclude" ).isPresent() );

        assertTrue( inputTypeConfig.optional( "include" ).isPresent() );

        assertTrue( inputTypeConfig.optional( "allowHeadings" ).isPresent() );
    }

    @Test
    void testParseTextArea()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/textarea-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "TextArea", input.getInputType().toString() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertEquals( 11, inputTypeConfig.optional( "maxLength" ).map( GenericValue::asInteger ).orElse( null ) );
        assertTrue( inputTypeConfig.optional( "showCounter" ).map( GenericValue::asBoolean ).orElse( false ) );
    }

    @Test
    void testParseDate()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/date-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "Date", input.getInputType().toString() );
        assertEquals( "My Date", input.getLabel() );
        assertEquals( "mydate", input.getName() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertEquals( "2025-08-29", inputTypeConfig.property( "default" ).asString() );
    }

    @Test
    void testParseInstant()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/instant-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "Instant", input.getInputType().toString() );
        assertEquals( "My Instant", input.getLabel() );
        assertEquals( "myInstant", input.getName() );
    }

    @Test
    void testParseDateTime()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/datetime-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "DateTime", input.getInputType().toString() );
        assertEquals( "My DateTime", input.getLabel() );
        assertEquals( "myDateTime", input.getName() );
    }

    @Test
    void testParseTime()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/time-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "Time", input.getInputType().toString() );
        assertEquals( "My Time", input.getLabel() );
        assertEquals( "mytime", input.getName() );
    }

    @Test
    void testParseCheckBox()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/checkbox-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "CheckBox", input.getInputType().toString() );
        assertEquals( "My Checkbox", input.getLabel() );
        assertEquals( "mycheckbox", input.getName() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertEquals( "right", inputTypeConfig.optional( "alignment" )

            .map( GenericValue::asString ).orElse( null ) );
    }

    @Test
    void testParseComboBox()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/combobox-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "ComboBox", input.getInputType().toString() );
        assertEquals( "My Combobox", input.getLabel() );
        assertEquals( "mycombobox", input.getName() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "options" ).isPresent() );
        assertEquals( "one", inputTypeConfig.property( "default" ).asString() );
    }


    @Test
    void testParseAttachmentUploader()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/attachmentuploader-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "AttachmentUploader", input.getInputType().toString() );
        assertEquals( "My AttachmentUploader", input.getLabel() );
        assertEquals( "myattachmentUploader", input.getName() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();
        assertTrue( inputTypeConfig.getProperties().isEmpty() );
    }

    @Test
    void testParseImageSelector()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/imageselector-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "ImageSelector", input.getInputType().toString() );
        assertEquals( "myImageSelector", input.getName() );
        assertEquals( "My ImageSelector", input.getLabel() );

        Occurrences occurrences = input.getOccurrences();
        assertEquals( 0, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "allowPath" ).isPresent() );

        assertTrue( inputTypeConfig.optional( "treeMode" ).isPresent() );

        assertTrue( inputTypeConfig.optional( "hideToggleIcon" ).isPresent() );
    }

    @Test
    void testParseMediaSelector()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/mediaselector-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "MediaSelector", input.getInputType().toString() );
        assertEquals( "myMediaSelector", input.getName() );
        assertEquals( "My MediaSelector", input.getLabel() );

        Occurrences occurrences = input.getOccurrences();
        assertEquals( 0, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "allowContentType" ).isPresent() );

        assertTrue( inputTypeConfig.optional( "allowPath" ).isPresent() );

        assertTrue( inputTypeConfig.optional( "treeMode" ).isPresent() );

        assertTrue( inputTypeConfig.optional( "hideToggleIcon" ).isPresent() );
    }

    @Test
    void testParseContentTypeFilter()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/contenttypetilter-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "ContentTypeFilter", input.getInputType().toString() );
        assertEquals( "myContentTypeFilter", input.getName() );
        assertEquals( "My ContentTypeFilter", input.getLabel() );

        Occurrences occurrences = input.getOccurrences();
        assertEquals( 0, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "context" ).isPresent() );
    }


    @Test
    void testParseTag()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/tag-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "Tag", input.getInputType().toString() );
        assertEquals( "myTag", input.getName() );
        assertEquals( "My Tag", input.getLabel() );

        final Occurrences occurrences = input.getOccurrences();
        assertEquals( 0, occurrences.getMinimum() );
        assertEquals( 0, occurrences.getMaximum() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.getProperties().isEmpty() );
    }

    @Test
    void testParseLong()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/long-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "Long", input.getInputType().toString() );
        assertEquals( "myLong", input.getName() );
        assertEquals( "My Long", input.getLabel() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertEquals( 350, inputTypeConfig.property( "min" ).asLong() );
        assertEquals( 123456789, inputTypeConfig.property( "max" ).asLong() );
        assertEquals( 1000, inputTypeConfig.property( "default" ).asLong() );
    }

    @Test
    void testParseGeoPoint()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/geopoint-type.yml" );

        final Input input = parser.parse( yaml, InputYml.class, CURRENT_APPLICATION ).convertToInput();

        assertEquals( "GeoPoint", input.getInputType().toString() );
        assertEquals( "myGeoPoint", input.getName() );
        assertEquals( "My GeoPoint", input.getLabel() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();
        assertEquals( "51.5,-0.1", inputTypeConfig.property( "default" ).asString() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlTypeParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
