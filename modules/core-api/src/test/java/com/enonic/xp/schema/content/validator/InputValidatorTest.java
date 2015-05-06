package com.enonic.xp.schema.content.validator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.xp.content.ContentPath;
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

import static com.enonic.xp.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public class InputValidatorTest
{
    private ContentType contentType;

    private InputValidator inputValidator;

    @Before
    public void before()
    {
        contentType = createContentTypeForAllInputTypes( ContentTypeName.audioMedia() );
        inputValidator = InputValidator.
            newInputValidator().
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
        data.addHtmlPart( "tinyMce", "<stuff>staff</stuff>" );
        data.addString( "phone", "+4797773223" );
        data.addString( "tag", "myTag" );
        data.addReference( "contentSelector", new Reference( new NodeId() ) );
        data.addString( "contentTypeFilter", "article" );
        data.addString( "moduleConfigurator", "my config here" );
        data.addDouble( "double", 1.1d );
        data.addLong( "long", 12345678910l );
        data.addStrings( "stringArray", "a", "b", "c" );
        data.addLocalDateTime( "localDateTime", LocalDateTime.parse( "2015-01-14T10:00:00" ) );
        data.addInstant( "dateTime", DateTimeFormatter.ISO_DATE_TIME.parse( "2015-01-15T10:15:00+02:00", Instant::from ) );
        data.addLocalDate( "date", LocalDate.parse( "2015-01-15" ) );
        data.addLocalTime( "time", LocalTime.parse( "10:00:32.123" ) );
        data.addGeoPoint( "geoPoint", GeoPoint.from( "-45,34" ) );
        data.addHtmlPart( "htmlArea", "<h1>test</h1>" );
        data.addString( "xml", "<xml><car><color>blue</color></car></xml>" );

        //Validates the correct data
        inputValidator.validate( data.getRoot() );
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
        invalidData.addLong( "color", Long.parseLong( "FFFFFF", 16 ) );
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
        invalidData.addDouble( "phone", 1.0d );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "tag", 1.0d );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "phone", 1.0d );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "contentSelector", 1.0d );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        //TODO ContentSelectConfig should check the content types
//        final Content referredContent = this.contentService.create( CreateContentParams.create().
//                    contentData( new PropertyTree() ).
//                    displayName( "Invalid Referred content" ).
//                    parent( ContentPath.ROOT ).
//                    type( ContentTypeName.shortcut() ).
//                    build() );
//        final Reference invalidReference = Reference.from( referredContent.getId().toString() );
//        invalidData = new PropertyTree();
//        invalidData.addReference( "contentSelector", invalidReference );
//        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "contentTypeFilter", 1.0d );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        //TODO ModuleConfigurator should check the input type
//        invalidData = new PropertyTree();
//        invalidData.addDouble( "moduleConfigurator", 1.0d );
//        validateIncorrectInputType( invalidData );

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
        invalidData.addString( "htmlArea", "<p>paragraph</p>" );
        validateIncorrectInputType( invalidData );

        //Validates an incorrect value
        invalidData = new PropertyTree();
        invalidData.addHtmlPart( "xml", "<elem>element</elem>" );
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
            inputValidator.validate( invalidData.getRoot() );
        }
        catch ( InvalidDataException e )
        {
            invalidDataExceptionThrown = true;
        }
        assertTrue( invalidDataExceptionThrown );
    }

    @Test
    @Ignore("ContentTypeName.isUnstructured() method is incorrect or define a recursive method ContentTypeName.isUnstructured()." +
        " To discuss")
    public void validate_incorrect_unstructured()
    {
        contentType = createContentTypeForAllInputTypes( ContentTypeName.unstructured() );
        inputValidator = InputValidator.
            newInputValidator().
            contentType( contentType ).
            build();

        //Validates an incorrect value
        PropertyTree invalidData = new PropertyTree();
        invalidData.addLong( "textLine", 1l );
        inputValidator.validate( invalidData.getRoot() );
    }

    @Test
    public void validate_unmapped_properties()
    {
        //Validates, with the default validator, data with an unmapped property
        PropertyTree invalidData = new PropertyTree();
        invalidData.addString( "unmappedProperty", "aValue" );
        inputValidator.validate( invalidData.getRoot() );

        //Validates, with a validator failing on unmapped properties, data with an unmapped property
        inputValidator = InputValidator.
            newInputValidator().
            contentType( contentType ).
            requireMappedProperties( true ).
            build();
        boolean invalidDataExceptionThrown = false;
        try
        {
            inputValidator.validate( invalidData.getRoot() );
        }
        catch ( InvalidDataException e )
        {
            invalidDataExceptionThrown = true;
        }
        assertTrue( invalidDataExceptionThrown );
    }

    protected ContentType createContentTypeForAllInputTypes( final ContentTypeName superType )
    {
        final FormItemSet set = FormItemSet.newFormItemSet().
            name( "set" ).
            addFormItem( Input.create().
                name( "setString" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "setDouble" ).
                inputType( InputTypes.DOUBLE ).
                build() ).
            build();
        return newContentType().
            superType( superType ).
            name( "myContentType" ).
            addFormItem( Input.create().
                name( "textLine" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "stringArray" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "double" ).
                inputType( InputTypes.DOUBLE ).
                build() ).
            addFormItem( Input.create().
                name( "long" ).
                inputType( InputTypes.LONG ).
                build() ).
            addFormItem( Input.create().
                name( "color" ).
                inputType( InputTypes.COLOR ).
                build() ).
            addFormItem( Input.create().
                name( "comboBox" ).
                inputType( InputTypes.COMBO_BOX ).
                inputTypeConfig( ComboBoxConfig.create().
                    addOption( "label1", "value1" ).
                    addOption( "label2", "value2" ).
                    addOption( "label3", "value3" ).
                    build() ).
                build() ).
            addFormItem( Input.create().
                name( "checkbox" ).
                inputType( InputTypes.CHECKBOX ).
                build() ).
            addFormItem( Input.create().
                name( "tinyMce" ).
                inputType( InputTypes.TINY_MCE ).
                build() ).
            addFormItem( Input.create().
                name( "phone" ).
                inputType( InputTypes.PHONE ).
                build() ).
            addFormItem( Input.create().
                name( "tag" ).
                inputType( InputTypes.TAG ).
                build() ).
            addFormItem( Input.create().
                name( "contentSelector" ).
                inputType( InputTypes.CONTENT_SELECTOR ).
                inputTypeConfig( ContentSelectorConfig.create().
                    addAllowedContentType( ContentTypeName.folder() ).
                    relationshipType( RelationshipTypeName.REFERENCE ).
                    build() ).
                build() ).
            addFormItem( Input.create().
                name( "contentTypeFilter" ).
                inputType( InputTypes.CONTENT_TYPE_FILTER ).
                build() ).
            addFormItem( Input.create().
                name( "moduleConfigurator" ).
                inputType( InputTypes.MODULE_CONFIGURATOR ).
                build() ).
            addFormItem( Input.create().
                name( "date" ).
                inputType( InputTypes.DATE ).
                build() ).
            addFormItem( Input.create().
                name( "time" ).
                inputType( InputTypes.TIME ).
                build() ).
            addFormItem( Input.create().
                name( "geoPoint" ).
                inputType( InputTypes.GEO_POINT ).
                build() ).
            addFormItem( Input.create().
                name( "htmlArea" ).
                inputType( InputTypes.HTML_AREA ).
                build() ).
            addFormItem( Input.create().
                name( "xml" ).
                inputType( InputTypes.XML ).
                build() ).
            addFormItem( Input.create().
                name( "localDateTime" ).
                inputType( InputTypes.DATE_TIME ).
                inputTypeConfig( DateTimeConfig.create().
                    withTimezone( false ).
                    build() ).
                build() ).
            addFormItem( Input.create().
                name( "dateTime" ).
                inputType( InputTypes.DATE_TIME ).
                inputTypeConfig( DateTimeConfig.create().
                    withTimezone( true ).
                    build() ).
                build() ).
            addFormItem( set ).
            build();
    }
}
