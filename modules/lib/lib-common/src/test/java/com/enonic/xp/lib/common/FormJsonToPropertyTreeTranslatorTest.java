package com.enonic.xp.lib.common;

import java.net.URL;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormJsonToPropertyTreeTranslatorTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void all_input_types()
        throws Exception
    {
        final JsonNode node = loadJson( "allInputTypes" );
        final PropertyTree data = new FormJsonToPropertyTreeTranslator( createFormForAllInputTypes(), true ).translate( node );

        final Property media = data.getProperty( "media" );
        assertNotNull( media );
        assertEquals( ValueTypes.PROPERTY_SET.getName(), media.getType().getName() );
    }

    @Test
    public void item_not_allowed_in_form()
        throws Exception
    {
        final JsonNode node = loadJson( "propertyNotInForm" );

        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new FormJsonToPropertyTreeTranslator( createFormForAllInputTypes(), true ).translate( node );
        });
        assertEquals( "No mapping defined for property cheesecake with value not allowed", ex.getMessage());
    }

    @Test
    public void map_array_values()
        throws Exception
    {
        final JsonNode node = loadJson( "allInputTypes" );

        final PropertyTree data = new FormJsonToPropertyTreeTranslator( null, false ).translate( node );

        final Property myArray = data.getProperty( "stringArray" );
        assertNotNull( myArray );
        assertEquals( ValueTypes.STRING.getName(), myArray.getType().getName() );

        final Property myArray0 = data.getProperty( "stringArray[0]" );
        assertNotNull( myArray0 );

        final Property myArray1 = data.getProperty( "stringArray[1]" );
        assertNotNull( myArray1 );

        final Property myArray2 = data.getProperty( "stringArray[2]" );
        assertNotNull( myArray2 );
    }

    @Test
    public void boolean_value()
        throws Exception
    {
        final JsonNode node = loadJson( "allInputTypes" );

        final PropertyTree data = new FormJsonToPropertyTreeTranslator( null, false ).translate( node );

        final Property property = data.getProperty( "checkbox" );

        assertTrue( property.getValue().isBoolean());
        assertEquals( true, property.getBoolean());
    }

    @Test
    public void map_dateTime()
        throws Exception
    {
        final JsonNode node = loadJson( "allInputTypes" );

        final PropertyTree data = new FormJsonToPropertyTreeTranslator( createFormForAllInputTypes(), true ).translate( node );

        final Property noTimezone = data.getProperty( "localDateTime" );
        assertNotNull( noTimezone );
        assertEquals( ValueTypes.LOCAL_DATE_TIME.getName(), noTimezone.getType().getName() );

        final Property timezoned = data.getProperty( "dateTime" );
        assertNotNull( timezoned );
        assertEquals( ValueTypes.DATE_TIME.getName(), timezoned.getType().getName() );
    }

    @Test
    public void map_optionSet()
        throws Exception
    {
        final JsonNode node = loadJson( "allInputTypes" );

        final PropertyTree data = new FormJsonToPropertyTreeTranslator( createFormForAllInputTypes(), true ).translate( node );

        final Property optionSet = data.getProperty( "myOptionSet" );
        assertNotNull( optionSet );
        assertEquals( ValueTypes.PROPERTY_SET.getName(), optionSet.getType().getName() );

        final Property optionSetSelection1 = optionSet.getSet().getProperty( "_selected", 0 );
        assertNotNull( optionSetSelection1 );
        assertEquals( ValueTypes.STRING.getName(), optionSetSelection1.getType().getName() );
        assertEquals( "myOptionSetOption1", optionSetSelection1.getString() );

        final Property optionSetSelection2 = optionSet.getSet().getProperty( "_selected", 1 );
        assertNotNull( optionSetSelection2 );
        assertEquals( ValueTypes.STRING.getName(), optionSetSelection2.getType().getName() );
        assertEquals( "myOptionSetOption2", optionSetSelection2.getString() );

        final Property optionSetOption1 = optionSet.getSet().getProperty( "myOptionSetOption1" );
        assertNotNull( optionSetOption1 );
        assertEquals( ValueTypes.PROPERTY_SET.getName(), optionSetOption1.getType().getName() );

        final Property optionSetOption1TextLine1 = optionSetOption1.getSet().getProperty( "myTextLine1" );
        assertNotNull( optionSetOption1TextLine1 );
        assertEquals( ValueTypes.STRING.getName(), optionSetOption1TextLine1.getType().getName() );
        assertEquals( "My Text 1", optionSetOption1TextLine1.getString() );
    }

    @Test
    public void translateFormWithFieldSet()
        throws Exception
    {
        final JsonNode node = loadJson( "fieldset" );
        final PropertyTree data = new FormJsonToPropertyTreeTranslator( createFormForFieldSet(), true ).translate( node );

        final Property key = data.getProperty( "attributes.key" );
        final Property numberProp = data.getProperty( "attributes.number" );

        assertEquals( ValueTypes.STRING.getName(), key.getType().getName() );
        assertEquals( "value", key.getValue().asString() );
        assertEquals( ValueTypes.LONG.getName(), numberProp.getType().getName() );
        assertEquals( 42L, numberProp.getValue().asLong().longValue() );
    }

    private JsonNode loadJson( final String name )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        assertNotNull( url, "File [" + resource + "] not found" );
        return MAPPER.readTree( url );
    }

    private Form createFormForAllInputTypes()
    {
        final FormItemSet set = FormItemSet.create().
            name( "set" ).
            addFormItem( Input.create().
                name( "setString" ).
                label( "String" ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "setDouble" ).
                label( "Double" ).
                inputType( InputTypeName.DOUBLE ).
                build() ).
            build();

        final FormOptionSet formOptionSet = FormOptionSet.create().
            name( "myOptionSet" ).
            label( "My option set" ).
            addOptionSetOption( FormOptionSetOption.create().name( "myOptionSetOption1" ).label( "option label1" ).
                addFormItem(
                    Input.create().name( "myTextLine1" ).label( "myTextLine1" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).
            addOptionSetOption( FormOptionSetOption.create().name( "myOptionSetOption2" ).label( "option label2" ).
                addFormItem(
                    Input.create().name( "myTextLine2" ).label( "myTextLine2" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).
            build();

        return Form.create().
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
                inputTypeProperty( InputTypeProperty.create( "option", "label1" ).attribute( "value", "value1" ).build() ).
                inputTypeProperty( InputTypeProperty.create( "option", "label2" ).attribute( "value", "value2" ).build() ).
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
                inputTypeProperty( InputTypeProperty.create( "allowedContentType", ContentTypeName.folder().toString() ).build() ).
                inputTypeProperty( InputTypeProperty.create( "relationshipType", RelationshipTypeName.REFERENCE.toString() ).build() ).
                build() ).
            addFormItem( Input.create().
                name( "contentTypeFilter" ).
                label( "Content type filter" ).
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
                inputTypeProperty( InputTypeProperty.create( "timezone", "false" ).build() ).
                build() ).
            addFormItem( Input.create().
                name( "dateTime" ).
                label( "Datetime" ).
                inputType( InputTypeName.DATE_TIME ).
                inputTypeProperty( InputTypeProperty.create( "timezone", "true" ).build() ).
                build() ).
            addFormItem( Input.create().
                name( "media" ).
                label( "Image Uploader" ).
                inputType( InputTypeName.IMAGE_UPLOADER ).
                build() ).
            addFormItem( set ).
            addFormItem( formOptionSet ).
            build();
    }

    private Form createFormForFieldSet()
    {
        final FieldSet fieldSet1 = FieldSet.create().
            name( "properties" ).
            label( "Properties" ).
            addFormItem( Input.create().
                name( "number" ).
                label( "Number" ).
                inputType( InputTypeName.LONG ).
                build() ).
            build();

        final FormItemSet itemSet = FormItemSet.create().
            name( "attributes" ).
            occurrences( 0, 0 ).
            addFormItem( Input.create().
                name( "key" ).
                label( "Key" ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( fieldSet1 ).
            build();

        final FieldSet fieldSet = FieldSet.create().
            name( "attributes" ).
            label( "Attributes" ).
            addFormItem( itemSet ).
            build();

        return Form.create().
            addFormItem( fieldSet ).
            build();
    }
}

