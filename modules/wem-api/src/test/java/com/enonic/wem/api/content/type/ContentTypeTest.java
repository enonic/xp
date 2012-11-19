package com.enonic.wem.api.content.type;


import org.junit.Test;

import com.enonic.wem.api.content.type.form.ComponentSetSubType;
import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.MockSubTypeFetcher;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.form.ComponentSetSubType.newComponentSetSubType;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.SubTypeReference.newSubTypeReference;
import static org.junit.Assert.*;

public class ContentTypeTest
{
    @Test
    public void layout()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        FieldSet layout = FieldSet.newFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).build();
        contentType.addFormItem( layout );

        assertEquals( "eyeColour", contentType.getInput( "eyeColour" ).getPath().toString() );
    }

    @Test
    public void layout_inside_componentSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        FieldSet layout = FieldSet.newFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).build();
        FormItemSet myComponentSet = newFormItemSet().name( "myFieldSet" ).add( layout ).build();
        contentType.addFormItem( myComponentSet );

        assertEquals( "myFieldSet.eyeColour", contentType.getInput( "myFieldSet.eyeColour" ).getPath().toString() );
    }

    @Test
    public void address()
    {
        FormItemSet componentSet = newFormItemSet().name( "address" ).build();
        componentSet.add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() );

        ContentType contentType = new ContentType();
        contentType.addFormItem( newInput().name( "title" ).type( InputTypes.TEXT_LINE ).build() );
        contentType.addFormItem( componentSet );

        assertEquals( "title", contentType.getInput( "title" ).getPath().toString() );
        assertEquals( "address.label", contentType.getInput( "address.label" ).getPath().toString() );
        assertEquals( "address.street", contentType.getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.postalNo", contentType.getInput( "address.postalNo" ).getPath().toString() );
        assertEquals( "address.country", contentType.getInput( "address.country" ).getPath().toString() );
    }

    @Test
    public void subTypeReferencesToComponents()
    {
        // setup
        Module module = Module.newModule().name( "myModule" ).build();

        ComponentSetSubType subType = newComponentSetSubType().module( module ).componentSet(
            newFormItemSet().name( "address" ).add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addFormItem( newSubTypeReference( subType ).name( "home" ).build() );
        cty.addFormItem( newSubTypeReference( subType ).name( "cabin" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        // exercise
        cty.subTypeReferencesToFormItems( subTypeFetcher );

        // verify:
        assertEquals( "home.street", cty.getInput( "home.street" ).getPath().toString() );
        assertEquals( "cabin.street", cty.getInput( "cabin.street" ).getPath().toString() );
    }

    @Test
    public void subTypeReferencesToComponents_layout()
    {
        // setup
        Module module = Module.newModule().name( "myModule" ).build();

        ComponentSetSubType subType = newComponentSetSubType().module( module ).componentSet( newFormItemSet().name( "address" ).add(
            FieldSet.newFieldSet().label( "My Field Set" ).name( "fieldSet" ).add(
                newInput().name( "myFieldInLayout" ).label( "MyFieldInLayout" ).type( InputTypes.TEXT_LINE ).build() ).build() ).add(
            newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
            newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).add(
            newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() ).add(
            newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.addFormItem( newSubTypeReference( subType ).name( "home" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        // exercise
        contentType.subTypeReferencesToFormItems( subTypeFetcher );

        // verify:
        assertEquals( "home.street", contentType.getInput( "home.street" ).getPath().toString() );
        assertEquals( "home.myFieldInLayout", contentType.getInput( "home.myFieldInLayout" ).getPath().toString() );
    }


    @Test
    public void subTypeReferencesToComponents_throws_exception_when_subType_is_not_of_expected_type()
    {
        // setup
        Module module = Module.newModule().name( "myModule" ).build();

        ComponentSetSubType subType = newComponentSetSubType().module( module ).componentSet(
            newFormItemSet().name( "address" ).add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addFormItem( newSubTypeReference().name( "home" ).typeInput().subType( subType.getQualifiedName() ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        // exercise
        try
        {
            cty.subTypeReferencesToFormItems( subTypeFetcher );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "SubType expected to be of type InputSubType: ComponentSetSubType", e.getMessage() );
        }
    }

    @Test
    public void fieldSet_in_FieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        FormItemSet componentSet =
            newFormItemSet().name( "top-fieldSet" ).add( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newFormItemSet().name( "inner-fieldSet" ).add(
                    newInput().name( "myInnerField" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();
        contentType.addFormItem( componentSet );

        assertEquals( "top-fieldSet", contentType.getFormItemSet( "top-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.myField", contentType.getInput( "top-fieldSet.myField" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet", contentType.getFormItemSet( "top-fieldSet.inner-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet.myInnerField",
                      contentType.getInput( "top-fieldSet.inner-fieldSet.myInnerField" ).getPath().toString() );
    }
}
