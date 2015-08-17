package com.enonic.xp.core.impl.content.validate;

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
import com.enonic.xp.inputtype.InputTypeValidationException;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypes;
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
            inputTypeResolver( InputTypes.BUILTIN ).
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
        data.addString( "comboBox", "value2" );
        data.addBoolean( "checkbox", true );
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
        data.addString( "htmlArea", "<stuff>staff</stuff>" );

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
        invalidData.addDouble( "htmlArea", 1.0d );
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
        catch ( final InputTypeValidationException e )
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
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "setDouble" ).
                label( "Set double" ).
                inputType( InputTypeName.DOUBLE ).
                build() ).
            build();
        return ContentType.create().
            superType( superType ).
            name( "myContentType" ).
            addFormItem( Input.create().
                name( "textLine" ).
                label( "Textline" ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "stringArray" ).
                label( "String array" ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "double" ).
                label( "Double" ).
                inputType( InputTypeName.DOUBLE ).
                build() ).
            addFormItem( Input.create().
                name( "long" ).
                label( "Long" ).
                inputType( InputTypeName.LONG ).
                build() ).
            addFormItem( Input.create().
                name( "comboBox" ).
                label( "Combobox" ).
                inputType( InputTypeName.COMBO_BOX ).
                inputTypeConfig( "option.value", "value1", "value2" ).
                inputTypeConfig( "option.label", "label1", "label1" ).
                build() ).
            addFormItem( Input.create().
                name( "checkbox" ).
                label( "Checkbox" ).
                inputType( InputTypeName.CHECK_BOX ).
                build() ).
            addFormItem( Input.create().
                name( "tag" ).
                label( "Tag" ).
                inputType( InputTypeName.TAG ).
                build() ).
            addFormItem( Input.create().
                name( "contentSelector" ).
                label( "Content selector" ).
                inputType( InputTypeName.CONTENT_SELECTOR ).
                inputTypeConfig( "relationshipType", RelationshipTypeName.REFERENCE.toString() ).
                inputTypeConfig( "allowedContentType", ContentTypeName.folder().toString() ).
                build() ).
            addFormItem( Input.create().
                name( "contentTypeFilter" ).
                label( "Contenttype filter" ).
                inputType( InputTypeName.CONTENT_TYPE_FILTER ).
                build() ).
            addFormItem( Input.create().
                name( "siteConfigurator" ).
                inputType( InputTypeName.SITE_CONFIGURATOR ).
                label( "Site configurator" ).
                build() ).
            addFormItem( Input.create().
                name( "date" ).
                label( "Date" ).
                inputType( InputTypeName.DATE ).
                build() ).
            addFormItem( Input.create().
                name( "time" ).
                label( "Time" ).
                inputType( InputTypeName.TIME ).
                build() ).
            addFormItem( Input.create().
                name( "geoPoint" ).
                label( "Geo point" ).
                inputType( InputTypeName.GEO_POINT ).
                build() ).
            addFormItem( Input.create().
                name( "htmlArea" ).
                label( "Html area" ).
                inputType( InputTypeName.HTML_AREA ).
                build() ).
            addFormItem( Input.create().
                name( "localDateTime" ).
                label( "Local datetime" ).
                inputType( InputTypeName.DATE_TIME ).
                inputTypeConfig( "timezone", "false" ).
                build() ).
            addFormItem( Input.create().
                name( "dateTime" ).
                label( "Datetime" ).
                inputType( InputTypeName.DATE_TIME ).
                inputTypeConfig( "timezone", "true" ).
                build() ).
            addFormItem( set ).
            build();
    }
}
