package com.enonic.xp.lib.content.mapper;

import java.net.URL;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

import static org.junit.Assert.*;

public class JsonToPropertyTreeTranslatorTest
{
    final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void all_input_types()
        throws Exception
    {
        final JsonNode node = loadJson( "allInputTypes" );

        JsonToPropertyTreeTranslator.create().
            mode( JsonToPropertyTreeTranslator.Mode.STRICT ).
            formItems( createFormForAllInputTypes().getFormItems() ).
            build().
            translate( node );
    }

    @Test
    public void map_array_values()
        throws Exception
    {
        final JsonNode node = loadJson( "allInputTypes" );

        final PropertyTree data = JsonToPropertyTreeTranslator.create().
            mode( JsonToPropertyTreeTranslator.Mode.LENIENT ).
            build().
            translate( node );

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
    public void map_dateTime()
        throws Exception
    {
        final JsonNode node = loadJson( "allInputTypes" );

        final PropertyTree data = JsonToPropertyTreeTranslator.create().
            formItems( createFormForAllInputTypes().getFormItems() ).
            mode( JsonToPropertyTreeTranslator.Mode.LENIENT ).
            build().
            translate( node );

        final Property noTimezone = data.getProperty( "localDateTime" );
        assertNotNull( noTimezone );
        assertEquals( ValueTypes.LOCAL_DATE_TIME.getName(), noTimezone.getType().getName() );

        final Property timezoned = data.getProperty( "dateTime" );
        assertNotNull( timezoned );
        assertEquals( ValueTypes.DATE_TIME.getName(), timezoned.getType().getName() );
    }

    protected final JsonNode loadJson( final String name )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        assertNotNull( "File [" + resource + "] not found", url );
        return this.mapper.readTree( url );
    }

    private Form createFormForAllInputTypes()
    {
        final FormItemSet set = FormItemSet.create().
            name( "set" ).
            addFormItem( Input.create().
                name( "setString" ).
                label( "String" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                name( "setDouble" ).
                label( "Double" ).
                inputType( InputTypes.DOUBLE ).
                build() ).
            build();

        return Form.create().
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
                inputTypeConfig( "option.value1", "label1" ).
                inputTypeConfig( "option.value2", "label2" ).
                inputTypeConfig( "option.value3", "label3" ).
                build() ).
            addFormItem( Input.create().
                name( "checkbox" ).
                label( "Checkbox" ).
                inputType( InputTypes.CHECKBOX ).
                build() ).
            addFormItem( Input.create().
                name( "tinyMce" ).
                label( "tinyMce" ).
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
                inputTypeConfig( "allowedContentTypes", ContentTypeName.folder().toString() ).
                inputTypeConfig( "relationshipType", RelationshipTypeName.REFERENCE.toString() ).
                build() ).
            addFormItem( Input.create().
                name( "contentTypeFilter" ).
                label( "Content type filter" ).
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
                inputTypeConfig( "withTimezone", "false" ).
                build() ).
            addFormItem( Input.create().
                name( "dateTime" ).
                label( "Datetime" ).
                inputType( InputTypes.DATE_TIME ).
                inputTypeConfig( "withTimezone", "true" ).
                build() ).
            addFormItem( set ).
            build();
    }

}

