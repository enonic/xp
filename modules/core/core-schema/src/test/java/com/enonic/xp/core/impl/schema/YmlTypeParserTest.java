package com.enonic.xp.core.impl.schema;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.InjectableValues;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.core.impl.schema.mapper.CheckBoxYml;
import com.enonic.xp.core.impl.schema.mapper.ComboBoxYml;
import com.enonic.xp.core.impl.schema.mapper.ContentSelectorYml;
import com.enonic.xp.core.impl.schema.mapper.CustomSelectorYml;
import com.enonic.xp.core.impl.schema.mapper.DateTimeYml;
import com.enonic.xp.core.impl.schema.mapper.DateYml;
import com.enonic.xp.core.impl.schema.mapper.DoubleYml;
import com.enonic.xp.core.impl.schema.mapper.HtmlAreaYml;
import com.enonic.xp.core.impl.schema.mapper.RadioButtonYml;
import com.enonic.xp.core.impl.schema.mapper.TextAreaYml;
import com.enonic.xp.core.impl.schema.mapper.TextLineYml;
import com.enonic.xp.core.impl.schema.mapper.TimeYml;
import com.enonic.xp.data.Value;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputType;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YmlTypeParserTest
{
    private final YmlTypeParser parser = new YmlTypeParser();

    @Test
    void testParse()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/content-type.yml" );

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

    @Test
    void testParseRadioButton()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/radiobutton-type.yml" );

        final RadioButtonYml radioButtonYml = parser.parse( yaml, RadioButtonYml.class );

        Input input = radioButtonYml.convertToInput();

        assertEquals( "RadioButton", input.getInputType().toString() );

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );

        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isString() );
        assertEquals( "cookie", defaultValue.asString() );

        final Set<InputTypeProperty> options = input.getInputTypeConfig().getProperties( "option" );

        final Iterator<InputTypeProperty> iterator = options.iterator();
        final InputTypeProperty cookieOpt = iterator.next();
        assertNotNull( cookieOpt );
        assertEquals( "Cookie", cookieOpt.getValue() );
        assertEquals( "cookie", cookieOpt.getAttribute( "value" ) );
        assertEquals( "i18n.rbg.cookie", cookieOpt.getAttribute( "i18n" ) );

        final InputTypeProperty privacyOpt = iterator.next();
        assertNotNull( privacyOpt );
        assertEquals( "Privacy", privacyOpt.getValue() );
        assertEquals( "privacy", privacyOpt.getAttribute( "value" ) );
    }

    @Test
    void testParseTextLine()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/textline-type.yml" );

        final TextLineYml textLineYml = parser.parse( yaml, TextLineYml.class );

        Input input = textLineYml.convertToInput();

        assertEquals( "TextLine", input.getInputType().toString() );

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );

        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isString() );
        assertEquals( "000-00-0000", defaultValue.asString() );

        final InputTypeProperty maxLengthOpt = input.getInputTypeConfig().getProperty( "maxLength" );
        assertNotNull( maxLengthOpt );
        assertEquals( "11", maxLengthOpt.getValue() );
        assertTrue( maxLengthOpt.getAttributes().isEmpty() );

        final InputTypeProperty regexpOpt = input.getInputTypeConfig().getProperty( "regexp" );
        assertNotNull( regexpOpt );
        assertEquals( "\\\\b\\\\d{3}-\\\\d{2}-\\\\d{4}\\\\b", regexpOpt.getValue() );
        assertTrue( regexpOpt.getAttributes().isEmpty() );

        final Occurrences occurrences = input.getOccurrences();
        assertEquals( 1, occurrences.getMinimum() );
        assertEquals( 3, occurrences.getMaximum() );
    }


    @Test
    void parseDouble()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/double-type.yml" );

        final DoubleYml doubleYml = parser.parse( yaml, DoubleYml.class );

        Input input = doubleYml.convertToInput();

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

    @Test
    void parseContentSelector()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/contentselector-type.yml" );

        final ContentSelectorYml contentSelectorYml = parser.parse( yaml, ContentSelectorYml.class );
        Input input = contentSelectorYml.convertToInput();

        assertEquals( "ContentSelector", input.getInputType().toString() );
        assertEquals( "searchResultPage", input.getName() );
        assertEquals( "Search result page", input.getLabel() );

        Occurrences occurrences = input.getOccurrences();
        assertEquals( 1, occurrences.getMinimum() );
        assertEquals( 1, occurrences.getMaximum() );

        final InputTypeConfig inputTypeConfig = input.getInputTypeConfig();

        final Set<InputTypeProperty> allowContentTypes = inputTypeConfig.getProperties( "allowContentType" );
        assertEquals( 2, allowContentTypes.size() );
        assertTrue( allowContentTypes.contains( InputTypeProperty.create( "allowContentType", "myapp:landing-page1" ).build() ) );
        assertTrue( allowContentTypes.contains( InputTypeProperty.create( "allowContentType", "myapp:landing-page2" ).build() ) );

        final Set<InputTypeProperty> allowPaths = inputTypeConfig.getProperties( "allowPath" );
        assertEquals( 2, allowPaths.size() );
        assertTrue( allowPaths.contains( InputTypeProperty.create( "allowPath", "${site}/people/" ).build() ) );
        assertTrue( allowPaths.contains( InputTypeProperty.create( "allowPath", "./*" ).build() ) );

        final InputTypeProperty treeMode = inputTypeConfig.getProperty( "treeMode" );
        assertNotNull( treeMode );
        assertEquals( "true", treeMode.getValue() );

        final InputTypeProperty hideToggleIcon = inputTypeConfig.getProperty( "hideToggleIcon" );
        assertNotNull( hideToggleIcon );
        assertEquals( "true", hideToggleIcon.getValue() );
    }

    @Test
    void parseCustomSelector()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/customselector-type.yml" );

        final InjectableValues injectableValues = new InjectableValues.Std().addValue( "applicationRelativeResolver",
                                                                                       new ApplicationRelativeResolver(
                                                                                           ApplicationKey.from( "myapp" ) ) );

        final CustomSelectorYml customSelectorYml = parser.parse( yaml, CustomSelectorYml.class, injectableValues );

        final Input input = customSelectorYml.convertToInput();

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );
        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isString() );
        assertEquals( "DefaultValue", defaultValue.asString() );

        assertEquals( InputTypeName.CUSTOM_SELECTOR, input.getInputType() );

        final InputTypeProperty serviceOpt = input.getInputTypeConfig().getProperty( "service" );
        assertNotNull( serviceOpt );
        assertEquals( "myapp/spotify-music-selector", serviceOpt.getValue() );
        assertTrue( serviceOpt.getAttributes().isEmpty() );

        final Set<InputTypeProperty> allowPaths = input.getInputTypeConfig().getProperties( "param" );
        assertEquals( 2, allowPaths.size() );
        assertTrue( allowPaths.contains( InputTypeProperty.create( "param", "classic" ).attribute( "value", "genre" ).build() ) );
        assertTrue( allowPaths.contains( InputTypeProperty.create( "param", "length" ).attribute( "value", "sortBy" ).build() ) );
    }

    @Test
    void testParseHtmlArea()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/htmlarea-type.yml" );

        final HtmlAreaYml htmlAreaYml = parser.parse( yaml, HtmlAreaYml.class );

        Input input = htmlAreaYml.convertToInput();

        assertEquals( "HtmlArea", input.getInputType().toString() );

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );

        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isString() );
        assertEquals( "<h3>Enter description here</h3>", defaultValue.asString() );

        final InputTypeProperty excludeOpt = input.getInputTypeConfig().getProperty( "exclude" );
        assertNotNull( excludeOpt );
        assertEquals( "*", excludeOpt.getValue() );
        assertTrue( excludeOpt.getAttributes().isEmpty() );

        final InputTypeProperty includeOpt = input.getInputTypeConfig().getProperty( "include" );
        assertNotNull( includeOpt );
        assertEquals( "JustifyLeft JustifyRight | Bold Italic", includeOpt.getValue() );
        assertTrue( includeOpt.getAttributes().isEmpty() );

        final InputTypeProperty allowHeadingsOpt = input.getInputTypeConfig().getProperty( "allowHeadings" );
        assertNotNull( allowHeadingsOpt );
        assertEquals( "h2 h4 h6", allowHeadingsOpt.getValue() );
        assertTrue( allowHeadingsOpt.getAttributes().isEmpty() );
    }

    @Test
    void testParseTextArea()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/textarea-type.yml" );

        final TextAreaYml inputYml = parser.parse( yaml, TextAreaYml.class );

        final Input input = inputYml.convertToInput();

        assertEquals( "TextArea", input.getInputType().toString() );

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );

        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isString() );
        assertEquals( "Default text goes here", defaultValue.asString() );

        final InputTypeProperty maxLengthOpt = input.getInputTypeConfig().getProperty( "maxLength" );
        assertNotNull( maxLengthOpt );
        assertEquals( "11", maxLengthOpt.getValue() );
        assertTrue( maxLengthOpt.getAttributes().isEmpty() );

        final InputTypeProperty showCounterOpt = input.getInputTypeConfig().getProperty( "showCounter" );
        assertNotNull( showCounterOpt );
        assertEquals( "true", showCounterOpt.getValue() );
        assertTrue( showCounterOpt.getAttributes().isEmpty() );
    }

    @Test
    void testParseDate()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/date-type.yml" );

        final DateYml inputYml = parser.parse( yaml, DateYml.class );

        final Input input = inputYml.convertToInput();

        assertEquals( "Date", input.getInputType().toString() );
        assertEquals( "My Date", input.getLabel() );
        assertEquals( "mydate", input.getName() );

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );

        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isDateType() );
        assertEquals( "2025-08-29", defaultValue.asString() );
    }

    @Test
    void testParseDateTime()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/datetime-type.yml" );

        final DateTimeYml inputYml = parser.parse( yaml, DateTimeYml.class );

        final Input input = inputYml.convertToInput();

        assertEquals( "DateTime", input.getInputType().toString() );
        assertEquals( "My DateTime", input.getLabel() );
        assertEquals( "mydatetime", input.getName() );

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );

        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isDateType() );
        assertEquals( "2025-08-29T07:44:27Z", defaultValue.asString() );

        final InputTypeProperty timezoneOpt = input.getInputTypeConfig().getProperty( "timezone" );
        assertNotNull( timezoneOpt );
        assertEquals( "true", timezoneOpt.getValue() );
        assertTrue( timezoneOpt.getAttributes().isEmpty() );
    }

    @Test
    void testParseTime()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/time-type.yml" );

        final TimeYml inputYml = parser.parse( yaml, TimeYml.class );

        final Input input = inputYml.convertToInput();

        assertEquals( "Time", input.getInputType().toString() );
        assertEquals( "My Time", input.getLabel() );
        assertEquals( "mytime", input.getName() );

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );

        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isJavaType( LocalTime.class ) );
        assertEquals( "13:22", defaultValue.asString() );
    }

    @Test
    void testParseCheckBox()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/checkbox-type.yml" );

        final CheckBoxYml inputYml = parser.parse( yaml, CheckBoxYml.class );

        final Input input = inputYml.convertToInput();

        assertEquals( "CheckBox", input.getInputType().toString() );
        assertEquals( "My Checkbox", input.getLabel() );
        assertEquals( "mycheckbox", input.getName() );

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );

        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isBoolean() );
        assertTrue( defaultValue.asBoolean() );

        final InputTypeProperty alignmentOpt = input.getInputTypeConfig().getProperty( "alignment" );
        assertNotNull( alignmentOpt );
        assertEquals( "right", alignmentOpt.getValue() );
        assertTrue( alignmentOpt.getAttributes().isEmpty() );
    }

    @Test
    void testParseComboBox()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/combobox-type.yml" );

        final ComboBoxYml inputYml = parser.parse( yaml, ComboBoxYml.class );

        final Input input = inputYml.convertToInput();

        assertEquals( "ComboBox", input.getInputType().toString() );
        assertEquals( "My Combobox", input.getLabel() );
        assertEquals( "mycombobox", input.getName() );

        final InputType inputType = InputTypes.BUILTIN.resolve( input.getInputType() );

        final Value defaultValue = inputType.createDefaultValue( input );
        assertTrue( defaultValue.isString() );
        assertEquals("one", defaultValue.asString() );

        final Set<InputTypeProperty> options = input.getInputTypeConfig().getProperties( "option" );

        final Iterator<InputTypeProperty> iterator = options.iterator();
        final InputTypeProperty cookieOpt = iterator.next();
        assertNotNull( cookieOpt );
        assertEquals( "Option One", cookieOpt.getValue() );
        assertEquals( "one", cookieOpt.getAttribute( "value" ) );

        final InputTypeProperty privacyOpt = iterator.next();
        assertNotNull( privacyOpt );
        assertEquals( "Option Two", privacyOpt.getValue() );
        assertEquals( "two", privacyOpt.getAttribute( "value" ) );

        final Occurrences occurrences = input.getOccurrences();
        assertEquals( 1, occurrences.getMinimum() );
        assertEquals( 2, occurrences.getMaximum() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlTypeParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
