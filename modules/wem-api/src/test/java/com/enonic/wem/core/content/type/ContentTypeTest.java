package com.enonic.wem.core.content.type;


import org.junit.Test;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.formitem.Component;
import com.enonic.wem.api.content.type.formitem.FieldSet;
import com.enonic.wem.api.content.type.formitem.FormItemSet;
import com.enonic.wem.api.content.type.formitem.FormItemSetTemplate;
import com.enonic.wem.api.content.type.formitem.FormItemSetTemplateBuilder;
import com.enonic.wem.api.content.type.formitem.MockTemplateFetcher;
import com.enonic.wem.api.content.type.formitem.TemplateReference;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.api.module.Module;

import static org.junit.Assert.*;

public class ContentTypeTest
{
    @Test
    public void layout()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        FieldSet layout = FieldSet.newFieldSet().label( "Personalia" ).name( "personalia" ).add(
            Component.newComponent().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() ).build();
        contentType.addFormItem( layout );

        assertEquals( "eyeColour", contentType.getComponent( "eyeColour" ).getPath().toString() );
    }

    @Test
    public void layout_inside_formItemSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        FieldSet layout = FieldSet.newFieldSet().label( "Personalia" ).name( "personalia" ).add(
            Component.newComponent().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() ).build();
        FormItemSet myFormItemSet = FormItemSet.newFormItemSet().name( "myFieldSet" ).add( layout ).build();
        contentType.addFormItem( myFormItemSet );

        assertEquals( "myFieldSet.eyeColour", contentType.getComponent( "myFieldSet.eyeColour" ).getPath().toString() );
    }

    @Test
    public void address()
    {
        FormItemSet formItemSet = FormItemSet.newFormItemSet().name( "address" ).build();
        formItemSet.addItem( Component.newComponent().name( "label" ).label( "Label" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newComponent().name( "street" ).label( "Street" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newComponent().name( "postalNo" ).label( "Postal No" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newComponent().name( "country" ).label( "Country" ).type( ComponentTypes.TEXT_LINE ).build() );

        ContentType contentType = new ContentType();
        contentType.addFormItem( Component.newComponent().name( "title" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.addFormItem( formItemSet );

        assertEquals( "title", contentType.getComponent( "title" ).getPath().toString() );
        assertEquals( "address.label", contentType.getComponent( "address.label" ).getPath().toString() );
        assertEquals( "address.street", contentType.getComponent( "address.street" ).getPath().toString() );
        assertEquals( "address.postalNo", contentType.getComponent( "address.postalNo" ).getPath().toString() );
        assertEquals( "address.country", contentType.getComponent( "address.country" ).getPath().toString() );
    }

    @Test
    public void templateReferencesToFormItems()
    {
        // setup
        Module module = Module.newModule().name( "myModule" ).build();

        FormItemSetTemplate template = FormItemSetTemplateBuilder.newFormItemSetTemplate().module( module ).formItemSet(
            FormItemSet.newFormItemSet().name( "address" ).add(
                Component.newComponent().name( "label" ).label( "Label" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                Component.newComponent().name( "street" ).label( "Street" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                Component.newComponent().name( "postalNo" ).label( "Postal No" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                Component.newComponent().name( "country" ).label( "Country" ).type( ComponentTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addFormItem( TemplateReference.newTemplateReference( template ).name( "home" ).build() );
        cty.addFormItem( TemplateReference.newTemplateReference( template ).name( "cabin" ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( template );

        // exercise
        cty.templateReferencesToFormItems( templateReferenceFetcher );

        // verify:
        assertEquals( "home.street", cty.getComponent( "home.street" ).getPath().toString() );
        assertEquals( "cabin.street", cty.getComponent( "cabin.street" ).getPath().toString() );
    }

    @Test
    public void templateReferencesToFormItems_layout()
    {
        // setup
        Module module = Module.newModule().name( "myModule" ).build();

        FormItemSetTemplate template = FormItemSetTemplateBuilder.newFormItemSetTemplate().module( module ).formItemSet(
            FormItemSet.newFormItemSet().name( "address" ).add( FieldSet.newFieldSet().label( "My Field Set" ).name( "fieldSet" ).add(
                Component.newComponent().name( "myFieldInLayout" ).label( "MyFieldInLayout" ).type(
                    ComponentTypes.TEXT_LINE ).build() ).build() ).add(
                Component.newComponent().name( "label" ).label( "Label" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                Component.newComponent().name( "street" ).label( "Street" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                Component.newComponent().name( "postalNo" ).label( "Postal No" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                Component.newComponent().name( "country" ).label( "Country" ).type( ComponentTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.addFormItem( TemplateReference.newTemplateReference( template ).name( "home" ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( template );

        // exercise
        contentType.templateReferencesToFormItems( templateReferenceFetcher );

        // verify:
        assertEquals( "home.street", contentType.getComponent( "home.street" ).getPath().toString() );
        assertEquals( "home.myFieldInLayout", contentType.getComponent( "home.myFieldInLayout" ).getPath().toString() );
    }


    @Test
    public void templateReferencesToFormItems_throws_exception_when_template_is_not_of_expected_type()
    {
        // setup
        Module module = Module.newModule().name( "myModule" ).build();

        FormItemSetTemplate formItemSetTemplate = FormItemSetTemplateBuilder.newFormItemSetTemplate().module( module ).formItemSet(
            FormItemSet.newFormItemSet().name( "address" ).add(
                Component.newComponent().name( "label" ).label( "Label" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                Component.newComponent().name( "street" ).label( "Street" ).type( ComponentTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addFormItem(
            TemplateReference.newBuilder().name( "home" ).typeComponent().template( formItemSetTemplate.getQualifiedName() ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( formItemSetTemplate );

        // exercise
        try
        {
            cty.templateReferencesToFormItems( templateReferenceFetcher );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Template expected to be of type ComponentTemplate: FormItemSetTemplate", e.getMessage() );
        }
    }

    @Test
    public void fieldSet_in_FieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        FormItemSet formItemSet = FormItemSet.newFormItemSet().name( "top-fieldSet" ).add(
            Component.newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
            FormItemSet.newFormItemSet().name( "inner-fieldSet" ).add(
                Component.newComponent().name( "myInnerField" ).type( ComponentTypes.TEXT_LINE ).build() ).build() ).build();
        contentType.addFormItem( formItemSet );

        assertEquals( "top-fieldSet", contentType.getFormItemSet( "top-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.myField", contentType.getComponent( "top-fieldSet.myField" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet", contentType.getFormItemSet( "top-fieldSet.inner-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet.myInnerField",
                      contentType.getComponent( "top-fieldSet.inner-fieldSet.myInnerField" ).getPath().toString() );
    }
}
