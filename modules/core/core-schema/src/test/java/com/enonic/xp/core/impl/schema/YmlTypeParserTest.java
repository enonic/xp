package com.enonic.xp.core.impl.schema;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.mapper.AttachmentUploaderYml;
import com.enonic.xp.core.impl.schema.mapper.CheckBoxYml;
import com.enonic.xp.core.impl.schema.mapper.ComboBoxYml;
import com.enonic.xp.core.impl.schema.mapper.ContentSelectorYml;
import com.enonic.xp.core.impl.schema.mapper.ContentTypeFilterYml;
import com.enonic.xp.core.impl.schema.mapper.CustomSelectorYml;
import com.enonic.xp.core.impl.schema.mapper.DateTimeYml;
import com.enonic.xp.core.impl.schema.mapper.DateYml;
import com.enonic.xp.core.impl.schema.mapper.DoubleYml;
import com.enonic.xp.core.impl.schema.mapper.GeoPointYml;
import com.enonic.xp.core.impl.schema.mapper.HtmlAreaYml;
import com.enonic.xp.core.impl.schema.mapper.ImageSelectorYml;
import com.enonic.xp.core.impl.schema.mapper.InstantYml;
import com.enonic.xp.core.impl.schema.mapper.LongYml;
import com.enonic.xp.core.impl.schema.mapper.MediaSelectorYml;
import com.enonic.xp.core.impl.schema.mapper.RadioButtonYml;
import com.enonic.xp.core.impl.schema.mapper.TagYml;
import com.enonic.xp.core.impl.schema.mapper.TextAreaYml;
import com.enonic.xp.core.impl.schema.mapper.TextLineYml;
import com.enonic.xp.core.impl.schema.mapper.TimeYml;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.GenericValue;
import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YmlTypeParserTest
{
    private final YmlParserBase parser = new YmlParserBase();

    private static final ApplicationKey CURRENT_APPLICATION = ApplicationKey.from( "myapp" );

    @Test
    void testParseRadioButton()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/radiobutton-type.yml" );

        final RadioButtonYml radioButtonYml = parser.parse( yaml, RadioButtonYml.class, CURRENT_APPLICATION );

        Input input = radioButtonYml.convertToInput();

        assertEquals( "RadioButton", input.getInputType().toString() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        final Optional<GenericValue> option = inputTypeConfig.optional( "option" );
        assertTrue( option.isPresent() );
        assertEquals( 2, option.get().asList().size() );

        assertTrue( inputTypeConfig.optional( "theme" ).isPresent() );
    }

    @Test
    void testParseTextLine()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/textline-type.yml" );

        final TextLineYml textLineYml = parser.parse( yaml, TextLineYml.class, CURRENT_APPLICATION );

        Input input = textLineYml.convertToInput();

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

        final DoubleYml doubleYml = parser.parse( yaml, DoubleYml.class, CURRENT_APPLICATION );

        Input input = doubleYml.convertToInput();

        assertEquals( "Double", input.getInputType().toString() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertEquals( 0, inputTypeConfig.optional( "min" ).map( GenericValue::asDouble ).orElse( null ) );
        assertEquals( 255, inputTypeConfig.optional( "max" ).map( GenericValue::asDouble ).orElse( null ) );
        assertTrue( inputTypeConfig.optional( "required" ).map( GenericValue::asBoolean ).orElse( false ) );
    }

    @Test
    void testParseContentSelector()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/contentselector-type.yml" );

        final ContentSelectorYml contentSelectorYml = parser.parse( yaml, ContentSelectorYml.class, CURRENT_APPLICATION );
        Input input = contentSelectorYml.convertToInput();

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

        final CustomSelectorYml customSelectorYml = parser.parse( yaml, CustomSelectorYml.class, CURRENT_APPLICATION );

        final Input input = customSelectorYml.convertToInput();

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

        final HtmlAreaYml htmlAreaYml = parser.parse( yaml, HtmlAreaYml.class, CURRENT_APPLICATION );

        Input input = htmlAreaYml.convertToInput();

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

        final TextAreaYml inputYml = parser.parse( yaml, TextAreaYml.class, CURRENT_APPLICATION );

        final Input input = inputYml.convertToInput();

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

        final DateYml inputYml = parser.parse( yaml, DateYml.class, CURRENT_APPLICATION );

        final Input input = inputYml.convertToInput();

        assertEquals( "Date", input.getInputType().toString() );
        assertEquals( "My Date", input.getLabel() );
        assertEquals( "mydate", input.getName() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "required" ).map( GenericValue::asBoolean ).orElse( false ) );
    }

    @Test
    void testParseInstant()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/instant-type.yml" );

        final InstantYml inputYml = parser.parse( yaml, InstantYml.class, CURRENT_APPLICATION );

        final Input input = inputYml.convertToInput();

        assertEquals( "Instant", input.getInputType().toString() );
        assertEquals( "My Instant", input.getLabel() );
        assertEquals( "myInstant", input.getName() );
    }

    @Test
    void testParseDateTime()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/datetime-type.yml" );

        final DateTimeYml inputYml = parser.parse( yaml, DateTimeYml.class, CURRENT_APPLICATION );

        final Input input = inputYml.convertToInput();

        assertEquals( "DateTime", input.getInputType().toString() );
        assertEquals( "My DateTime", input.getLabel() );
        assertEquals( "myDateTime", input.getName() );
    }

    @Test
    void testParseTime()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/time-type.yml" );

        final TimeYml inputYml = parser.parse( yaml, TimeYml.class, CURRENT_APPLICATION );

        final Input input = inputYml.convertToInput();

        assertEquals( "Time", input.getInputType().toString() );
        assertEquals( "My Time", input.getLabel() );
        assertEquals( "mytime", input.getName() );
    }

    @Test
    void testParseCheckBox()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/checkbox-type.yml" );

        final CheckBoxYml inputYml = parser.parse( yaml, CheckBoxYml.class, CURRENT_APPLICATION );

        final Input input = inputYml.convertToInput();

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

        final ComboBoxYml inputYml = parser.parse( yaml, ComboBoxYml.class, CURRENT_APPLICATION );

        final Input input = inputYml.convertToInput();

        assertEquals( "ComboBox", input.getInputType().toString() );
        assertEquals( "My Combobox", input.getLabel() );
        assertEquals( "mycombobox", input.getName() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "option" ).isPresent() );
        assertTrue( inputTypeConfig.optional( "required" ).isPresent() );
    }


    @Test
    void testParseAttachmentUploader()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/attachmentuploader-type.yml" );

        final AttachmentUploaderYml inputYml = parser.parse( yaml, AttachmentUploaderYml.class, CURRENT_APPLICATION );

        final Input input = inputYml.convertToInput();

        assertEquals( "AttachmentUploader", input.getInputType().toString() );
        assertEquals( "My AttachmentUploader", input.getLabel() );
        assertEquals( "myattachmentUploader", input.getName() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "disabled" ).isPresent() );
    }

    @Test
    void testParseImageSelector()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/imageselector-type.yml" );

        final ImageSelectorYml contentSelectorYml = parser.parse( yaml, ImageSelectorYml.class, CURRENT_APPLICATION );
        Input input = contentSelectorYml.convertToInput();

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

        final MediaSelectorYml contentSelectorYml = parser.parse( yaml, MediaSelectorYml.class, CURRENT_APPLICATION );
        Input input = contentSelectorYml.convertToInput();

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

        final ContentTypeFilterYml contentSelectorYml = parser.parse( yaml, ContentTypeFilterYml.class, CURRENT_APPLICATION );
        Input input = contentSelectorYml.convertToInput();

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

        final TagYml contentSelectorYml = parser.parse( yaml, TagYml.class, CURRENT_APPLICATION );
        final Input input = contentSelectorYml.convertToInput();

        assertEquals( "Tag", input.getInputType().toString() );
        assertEquals( "myTag", input.getName() );
        assertEquals( "My Tag", input.getLabel() );

        final Occurrences occurrences = input.getOccurrences();
        assertEquals( 0, occurrences.getMinimum() );
        assertEquals( 0, occurrences.getMaximum() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "disabled" ).isPresent() );
    }

    @Test
    void testParseLong()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/long-type.yml" );

        final LongYml inputYml = parser.parse( yaml, LongYml.class, CURRENT_APPLICATION );

        final Input input = inputYml.convertToInput();

        assertEquals( "Long", input.getInputType().toString() );
        assertEquals( "myLong", input.getName() );
        assertEquals( "My Long", input.getLabel() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "disabled" ).isPresent() );
    }

    @Test
    void testParseGeoPoint()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/geopoint-type.yml" );

        final GeoPointYml inputYml = parser.parse( yaml, GeoPointYml.class, CURRENT_APPLICATION );

        final Input input = inputYml.convertToInput();

        assertEquals( "GeoPoint", input.getInputType().toString() );
        assertEquals( "myGeoPoint", input.getName() );
        assertEquals( "My GeoPoint", input.getLabel() );

        final GenericValue inputTypeConfig = input.getInputTypeConfig();

        assertTrue( inputTypeConfig.optional( "disabled" ).isPresent() );

    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlTypeParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
