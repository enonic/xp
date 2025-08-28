package com.enonic.xp.core.impl.schema;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.InjectableValues;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.core.impl.schema.mapper.ContentSelectorYml;
import com.enonic.xp.core.impl.schema.mapper.CustomSelectorYml;
import com.enonic.xp.core.impl.schema.mapper.DoubleYml;
import com.enonic.xp.core.impl.schema.mapper.HtmlAreaYml;
import com.enonic.xp.core.impl.schema.mapper.RadioButtonYml;
import com.enonic.xp.core.impl.schema.mapper.TextLineYml;
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

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlTypeParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
