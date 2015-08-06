package com.enonic.xp.schema.content.validator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InvalidDataException;
import com.enonic.xp.form.inputtype.ComboBoxConfig;
import com.enonic.xp.form.inputtype.ContentSelectorConfig;
import com.enonic.xp.form.inputtype.DateTimeConfig;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class InputValidatorTest
{
    private InputValidator inputValidator;

    @Before
    public void before()
    {
        final ContentType contentType = createContentTypeForAllInputTypes( ContentTypeName.audioMedia() );
        this.inputValidator = InputValidator.
            create().
            contentType( contentType ).
            build();
    }

    @Test
    public void validate_correct_input_types()
    {
        //Creates a property set
        final PropertySet propertySet = new PropertySet();
        propertySet.addString( "setString", "ost" );
        propertySet.addDouble( "setDouble", 123d );

        //Creates the correct data to validate
        final PropertyTree data = new PropertyTree();
        data.addString( "textLine", "textLine" );
        data.addString( "color", "#12345" );
        data.addString( "comboBox", "value3" );
        data.addBoolean( "checkbox", true );
        data.addString( "tinyMce", "<stuff>staff</stuff>" );
        data.addString( "phone", "+4797773223" );
        data.addString( "tag", "myTag" );
        data.addReference( "contentSelector", new Reference( new NodeId() ) );
        data.addString( "contentTypeFilter", "article" );
        data.addString( "siteConfigurator", "my config here" );
        data.addDouble( "double", 1.1d );
        data.addLong( "long", 12345678910l );
        data.addStrings( "stringArray", "a", "b", "c" );
        data.addLocalDateTime( "localDateTime", LocalDateTime.parse( "2015-01-14T10:00:00" ) );
        data.addInstant( "dateTime", DateTimeFormatter.ISO_DATE_TIME.parse( "2015-01-15T10:15:00+02:00", Instant::from ) );
        data.addLocalDate( "date", LocalDate.parse( "2015-01-15" ) );
        data.addLocalTime( "time", LocalTime.parse( "10:00:32.123" ) );
        data.addGeoPoint( "geoPoint", GeoPoint.from( "-45,34" ) );
        data.addString( "htmlArea", "<h1>test</h1>" );

        //Validates the correct data
        inputValidator.validate( data );
    }

    @Test
    public void validate_incorrect_input_types()
    {
        //Validates an incorrect value
        PropertyTree invalidData = new PropertyTree();
        invalidData.addLong( "textLine", 1l );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addLong( "double", 1l );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "long", 1.0d );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addBoolean( "comboBox", true );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addString( "comboBox", "value4" );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "checkbox", 1.0d );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "tinyMce", 1.0d );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "tag", 1.0d );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "contentSelector", 1.0d );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "contentTypeFilter", 1.0d );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addLocalDateTime( "date", LocalDateTime.of( 2015, 03, 13, 10, 00, 0 ) );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addInstant( "time", Instant.now() );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addString( "geoPoint", "59.9127300, 10.7460900" );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addXml( "htmlArea", "<p>paragraph</p>" );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addLocalDate( "localDateTime", LocalDate.of( 2015, 03, 13 ) );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addLocalDate( "dateTime", LocalDate.of( 2015, 03, 13 ) );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        PropertySet invalidSet = new PropertySet();
        invalidSet.addDouble( "setString", 1.0d );
        invalidData.addSet( "set", invalidSet );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidSet = new PropertySet();
        invalidSet.addLong( "setDouble", 1l );
        invalidData.addSet( "set", invalidSet );
        validateIncorrectInputType( invalidData );
    }

    private void validateIncorrectInputType( PropertyTree invalidData )
    {
        boolean invalidDataExceptionThrown = false;
        try
        {
            inputValidator.validate( invalidData );
        }
        catch ( InvalidDataException e )
        {
            invalidDataExceptionThrown = true;
        }
        assertTrue( invalidDataExceptionThrown );
    }

    protected ContentType createContentTypeForAllInputTypes( final ContentTypeName superType )
    {
        final FormItemSet set = FormItemSet.create().
            name( "set" ).
            addFormItem( Input.create().
                name( "setString" ).
                label( "Set string" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "setDouble" ).
                label( "Set double" ).
                inputType( InputTypes.DOUBLE ).
                build() ).
            build();
        return ContentType.create().
            superType( superType ).
            name( "myContentType" ).
            addFormItem( Input.create().
                name( "textLine" ).
                label( "Textline" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "stringArray" ).
                label( "String array" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "double" ).
                label( "Double" ).
                inputType( InputTypes.DOUBLE ).
                build() ).
            addFormItem( Input.create().
                name( "long" ).
                label( "Long" ).
                inputType( InputTypes.LONG ).
                build() ).
            addFormItem( Input.create().
                name( "comboBox" ).
                label( "Combobox" ).
                inputType( InputTypes.COMBO_BOX ).
                inputTypeConfig( ComboBoxConfig.create().
                    addOption( "label1", "value1" ).
                    addOption( "label2", "value2" ).
                    addOption( "label3", "value3" ).
                    build() ).
                build() ).
            addFormItem( Input.create().
                name( "checkbox" ).
                label( "Checkbox" ).
                inputType( InputTypes.CHECKBOX ).
                build() ).
            addFormItem( Input.create().
                name( "tinyMce" ).
                label( "Tinymce" ).
                inputType( InputTypes.TINY_MCE ).
                build() ).
            addFormItem( Input.create().
                name( "tag" ).
                label( "Tag" ).
                inputType( InputTypes.TAG ).
                build() ).
            addFormItem( Input.create().
                name( "contentSelector" ).
                label( "Content selector" ).
                inputType( InputTypes.CONTENT_SELECTOR ).
                inputTypeConfig( ContentSelectorConfig.create().
                    addAllowedContentType( ContentTypeName.folder() ).
                    relationshipType( RelationshipTypeName.REFERENCE ).
                    build() ).
                build() ).
            addFormItem( Input.create().
                name( "contentTypeFilter" ).
                label( "Contenttype filter" ).
                inputType( InputTypes.CONTENT_TYPE_FILTER ).
                build() ).
            addFormItem( Input.create().
                name( "siteConfigurator" ).
                inputType( InputTypes.SITE_CONFIGURATOR ).
                label( "Site configurator" ).
                build() ).
            addFormItem( Input.create().
                name( "date" ).
                label( "Date" ).
                inputType( InputTypes.DATE ).
                build() ).
            addFormItem( Input.create().
                name( "time" ).
                label( "Time" ).
                inputType( InputTypes.TIME ).
                build() ).
            addFormItem( Input.create().
                name( "geoPoint" ).
                label( "Geo point" ).
                inputType( InputTypes.GEO_POINT ).
                build() ).
            addFormItem( Input.create().
                name( "htmlArea" ).
                label( "Html area" ).
                inputType( InputTypes.HTML_AREA ).
                build() ).
            addFormItem( Input.create().
                name( "localDateTime" ).
                label( "Local datetime" ).
                inputType( InputTypes.DATE_TIME ).
                inputTypeConfig( DateTimeConfig.create().
                    withTimezone( false ).
                    build() ).
                build() ).
            addFormItem( Input.create().
                name( "dateTime" ).
                label( "Datetime" ).
                inputType( InputTypes.DATE_TIME ).
                inputTypeConfig( DateTimeConfig.create().
                    withTimezone( true ).
                    build() ).
                build() ).
            addFormItem( set ).
            build();
    }
}
