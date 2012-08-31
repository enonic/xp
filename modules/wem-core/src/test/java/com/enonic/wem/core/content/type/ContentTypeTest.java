package com.enonic.wem.core.content.type;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplate;
import com.enonic.wem.core.content.type.configitem.MockTemplateFetcher;
import com.enonic.wem.core.content.type.configitem.TemplateReference;
import com.enonic.wem.core.content.type.configitem.VisualFieldSet;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.Field.newField;
import static com.enonic.wem.core.content.type.configitem.FieldSet.newFieldSet;
import static com.enonic.wem.core.content.type.configitem.FieldSetTemplateBuilder.newFieldSetTemplate;
import static com.enonic.wem.core.content.type.configitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.content.type.configitem.VisualFieldSet.newVisualFieldSet;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;

public class ContentTypeTest
{
    @Test
    public void visualFieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        VisualFieldSet visualFieldSet = newVisualFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newField().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() ).build();
        contentType.addConfigItem( visualFieldSet );

        assertEquals( "eyeColour", contentType.getField( "eyeColour" ).getPath().toString() );
    }

    @Test
    public void visualFieldSet_inside_fieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        VisualFieldSet visualFieldSet = newVisualFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newField().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() ).build();
        FieldSet myFieldSet = newFieldSet().name( "myFieldSet" ).add( visualFieldSet ).build();
        contentType.addConfigItem( myFieldSet );

        assertEquals( "myFieldSet.eyeColour", contentType.getField( "myFieldSet.eyeColour" ).getPath().toString() );
    }

    @Test
    public void address()
    {
        FieldSet fieldSet = newFieldSet().name( "address" ).build();
        fieldSet.addField( newField().name( "label" ).label( "Label" ).type( FieldTypes.TEXT_LINE ).build() );
        fieldSet.addField( newField().name( "street" ).label( "Street" ).type( FieldTypes.TEXT_LINE ).build() );
        fieldSet.addField( newField().name( "postalNo" ).label( "Postal No" ).type( FieldTypes.TEXT_LINE ).build() );
        fieldSet.addField( newField().name( "country" ).label( "Country" ).type( FieldTypes.TEXT_LINE ).build() );

        ContentType contentType = new ContentType();
        contentType.addConfigItem( newField().name( "title" ).type( FieldTypes.TEXT_LINE ).build() );
        contentType.addConfigItem( fieldSet );

        assertEquals( "title", contentType.getField( "title" ).getPath().toString() );
        assertEquals( "address.label", contentType.getField( "address.label" ).getPath().toString() );
        assertEquals( "address.street", contentType.getField( "address.street" ).getPath().toString() );
        assertEquals( "address.postalNo", contentType.getField( "address.postalNo" ).getPath().toString() );
        assertEquals( "address.country", contentType.getField( "address.country" ).getPath().toString() );
    }

    @Test
    public void templateReferencesToConfigItems()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        FieldSetTemplate template = newFieldSetTemplate().module( module ).fieldSet(
            newFieldSet().name( "address" ).add( newField().name( "label" ).label( "Label" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newField().name( "street" ).label( "Street" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newField().name( "postalNo" ).label( "Postal No" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newField().name( "country" ).label( "Country" ).type( FieldTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addConfigItem( newTemplateReference( template ).name( "home" ).build() );
        cty.addConfigItem( newTemplateReference( template ).name( "cabin" ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( template );

        // exercise
        cty.templateReferencesToConfigItems( templateReferenceFetcher );

        // verify:
        assertEquals( "home.street", cty.getField( "home.street" ).getPath().toString() );
        assertEquals( "cabin.street", cty.getField( "cabin.street" ).getPath().toString() );
    }

    @Test
    public void templateReferencesToConfigItems_visual_field_set()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        FieldSetTemplate template = newFieldSetTemplate().module( module ).fieldSet( newFieldSet().name( "address" ).add(
            newVisualFieldSet().label( "My Visual Field Set" ).name( "vfs" ).add(
                newField().name( "myFieldInVFS" ).label( "MyFieldInVFS" ).type( FieldTypes.TEXT_LINE ).build() ).build() ).add(
            newField().name( "label" ).label( "Label" ).type( FieldTypes.TEXT_LINE ).build() ).add(
            newField().name( "street" ).label( "Street" ).type( FieldTypes.TEXT_LINE ).build() ).add(
            newField().name( "postalNo" ).label( "Postal No" ).type( FieldTypes.TEXT_LINE ).build() ).add(
            newField().name( "country" ).label( "Country" ).type( FieldTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.addConfigItem( newTemplateReference( template ).name( "home" ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( template );

        // exercise
        contentType.templateReferencesToConfigItems( templateReferenceFetcher );

        // verify:
        assertEquals( "home.street", contentType.getField( "home.street" ).getPath().toString() );
        assertEquals( "home.myFieldInVFS", contentType.getField( "home.myFieldInVFS" ).getPath().toString() );
    }


    @Test
    public void templateReferencesToConfigItems_throws_exception_when_template_is_not_of_expected_type()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        FieldSetTemplate fieldSetTemplate = newFieldSetTemplate().module( module ).fieldSet(
            newFieldSet().name( "address" ).add( newField().name( "label" ).label( "Label" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newField().name( "street" ).label( "Street" ).type( FieldTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addConfigItem(
            TemplateReference.newBuilder().name( "home" ).typeField().template( fieldSetTemplate.getQualifiedName() ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( fieldSetTemplate );

        // exercise
        try
        {
            cty.templateReferencesToConfigItems( templateReferenceFetcher );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Template expected to be of type FIELD: FIELD_SET", e.getMessage() );
        }
    }

    @Test
    public void fieldSet_in_FieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        FieldSet fieldSet =
            newFieldSet().name( "top-fieldSet" ).add( newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).build() ).add(
                newFieldSet().name( "inner-fieldSet" ).add(
                    newField().name( "myInnerField" ).type( FieldTypes.TEXT_LINE ).build() ).build() ).build();
        contentType.addConfigItem( fieldSet );

        assertEquals( "top-fieldSet", contentType.getFieldSet( "top-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.myField", contentType.getField( "top-fieldSet.myField" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet", contentType.getFieldSet( "top-fieldSet.inner-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet.myInnerField",
                      contentType.getField( "top-fieldSet.inner-fieldSet.myInnerField" ).getPath().toString() );
    }
}
