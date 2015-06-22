package com.enonic.xp.core.impl.content;

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
import com.enonic.xp.form.inputtype.ComboBoxConfig;
import com.enonic.xp.form.inputtype.ContentSelectorConfig;
import com.enonic.xp.form.inputtype.DateTimeConfig;
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

        return Form.newForm().
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
                name( "siteConfigurator" ).
                inputType( InputTypes.SITE_CONFIGURATOR ).
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

