package com.enonic.wem.api.content.type;


import org.junit.Test;

import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.ComponentSetSubType;
import com.enonic.wem.api.content.type.component.ComponentSetSubTypeBuilder;
import com.enonic.wem.api.content.type.component.FieldSet;
import com.enonic.wem.api.content.type.component.MockSubTypeFetcher;
import com.enonic.wem.api.content.type.component.SubTypeReference;
import com.enonic.wem.api.content.type.component.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.component.Input.newInput;
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
        contentType.addComponent( layout );

        assertEquals( "eyeColour", contentType.getInput( "eyeColour" ).getPath().toString() );
    }

    @Test
    public void layout_inside_componentSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        FieldSet layout = FieldSet.newFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).build();
        ComponentSet myComponentSet = ComponentSet.newComponentSet().name( "myFieldSet" ).add( layout ).build();
        contentType.addComponent( myComponentSet );

        assertEquals( "myFieldSet.eyeColour", contentType.getInput( "myFieldSet.eyeColour" ).getPath().toString() );
    }

    @Test
    public void address()
    {
        ComponentSet componentSet = ComponentSet.newComponentSet().name( "address" ).build();
        componentSet.addInput( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.addInput( newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.addInput( newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.addInput( newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() );

        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().name( "title" ).type( InputTypes.TEXT_LINE ).build() );
        contentType.addComponent( componentSet );

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

        ComponentSetSubType subType = ComponentSetSubTypeBuilder.newComponentSetSubType().module( module ).componentSet(
            ComponentSet.newComponentSet().name( "address" ).add(
                newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addComponent( SubTypeReference.newSubTypeReference( subType ).name( "home" ).build() );
        cty.addComponent( SubTypeReference.newSubTypeReference( subType ).name( "cabin" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        // exercise
        cty.subTypeReferencesToComponents( subTypeFetcher );

        // verify:
        assertEquals( "home.street", cty.getInput( "home.street" ).getPath().toString() );
        assertEquals( "cabin.street", cty.getInput( "cabin.street" ).getPath().toString() );
    }

    @Test
    public void subTypeReferencesToComponents_layout()
    {
        // setup
        Module module = Module.newModule().name( "myModule" ).build();

        ComponentSetSubType subType = ComponentSetSubTypeBuilder.newComponentSetSubType().module( module ).componentSet(
            ComponentSet.newComponentSet().name( "address" ).add( FieldSet.newFieldSet().label( "My Field Set" ).name( "fieldSet" ).add(
                newInput().name( "myFieldInLayout" ).label( "MyFieldInLayout" ).type( InputTypes.TEXT_LINE ).build() ).build() ).add(
                newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.addComponent( SubTypeReference.newSubTypeReference( subType ).name( "home" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        // exercise
        contentType.subTypeReferencesToComponents( subTypeFetcher );

        // verify:
        assertEquals( "home.street", contentType.getInput( "home.street" ).getPath().toString() );
        assertEquals( "home.myFieldInLayout", contentType.getInput( "home.myFieldInLayout" ).getPath().toString() );
    }


    @Test
    public void subTypeReferencesToComponents_throws_exception_when_subType_is_not_of_expected_type()
    {
        // setup
        Module module = Module.newModule().name( "myModule" ).build();

        ComponentSetSubType subType = ComponentSetSubTypeBuilder.newComponentSetSubType().module( module ).componentSet(
            ComponentSet.newComponentSet().name( "address" ).add(
                newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.addComponent( SubTypeReference.newBuilder().name( "home" ).typeInput().subType( subType.getQualifiedName() ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        // exercise
        try
        {
            cty.subTypeReferencesToComponents( subTypeFetcher );
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
        ComponentSet componentSet = ComponentSet.newComponentSet().name( "top-fieldSet" ).add(
            newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() ).add(
            ComponentSet.newComponentSet().name( "inner-fieldSet" ).add(
                newInput().name( "myInnerField" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();
        contentType.addComponent( componentSet );

        assertEquals( "top-fieldSet", contentType.getComponentSet( "top-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.myField", contentType.getInput( "top-fieldSet.myField" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet", contentType.getComponentSet( "top-fieldSet.inner-fieldSet" ).getPath().toString() );
        assertEquals( "top-fieldSet.inner-fieldSet.myInnerField",
                      contentType.getInput( "top-fieldSet.inner-fieldSet.myInnerField" ).getPath().toString() );
    }
}
