package com.enonic.wem.api.content.type;


import org.junit.Test;

import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.FormItemSetSubType;
import com.enonic.wem.api.content.type.form.MockSubTypeFetcher;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.FormItemSetSubType.newFormItemSetSubType;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.SubTypeReference.newSubTypeReference;
import static org.junit.Assert.*;

public class ContentTypeTest
{
    @Test
    public void layout()
    {
        final ContentType contentType = newContentType().name( "test" ).build();
        FieldSet layout = FieldSet.newFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).build();
        contentType.form().addFormItem( layout );

        assertEquals( "eyeColour", contentType.form().getInput( "eyeColour" ).getPath().toString() );
    }

    @Test
    public void layout_inside_formItemSet()
    {
        final ContentType contentType = newContentType().name( "test" ).build();
        FieldSet layout = FieldSet.newFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).build();
        FormItemSet myFormItemSet = newFormItemSet().name( "mySet" ).add( layout ).build();
        contentType.form().addFormItem( myFormItemSet );

        assertEquals( "mySet.eyeColour", contentType.form().getInput( "mySet.eyeColour" ).getPath().toString() );
    }

    @Test
    public void address()
    {
        FormItemSet formItemSet = newFormItemSet().name( "address" ).build();
        formItemSet.add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() );

        final ContentType contentType = newContentType().
            addFormItem( newInput().name( "title" ).type( InputTypes.TEXT_LINE ).build() ).
            addFormItem( formItemSet ).
            build();

        assertEquals( "title", contentType.form().getInput( "title" ).getPath().toString() );
        assertEquals( "address.label", contentType.form().getInput( "address.label" ).getPath().toString() );
        assertEquals( "address.street", contentType.form().getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.postalNo", contentType.form().getInput( "address.postalNo" ).getPath().toString() );
        assertEquals( "address.country", contentType.form().getInput( "address.country" ).getPath().toString() );
    }

    @Test
    public void subTypeReferencesToFormItems()
    {
        // setup
        FormItemSetSubType subType = newFormItemSetSubType().module( ModuleName.from( "myModule" ) ).formItemSet(
            newFormItemSet().name( "address" ).add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        final ContentType cty = newContentType().
            addFormItem( newSubTypeReference( subType ).name( "home" ).build() ).
            addFormItem( newSubTypeReference( subType ).name( "cabin" ).build() ).
            build();

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        // exercise
        cty.form().subTypeReferencesToFormItems( subTypeFetcher );

        // verify:
        assertEquals( "home.street", cty.form().getInput( "home.street" ).getPath().toString() );
        assertEquals( "cabin.street", cty.form().getInput( "cabin.street" ).getPath().toString() );
    }

    @Test
    public void subTypeReferencesToFormItems_layout()
    {
        // setup
        FormItemSetSubType subType = newFormItemSetSubType().module( ModuleName.from( "myModule" ) ).formItemSet(
            newFormItemSet().name( "address" ).add( FieldSet.newFieldSet().label( "My Field Set" ).name( "fieldSet" ).add(
                newInput().name( "myFieldInLayout" ).label( "MyFieldInLayout" ).type( InputTypes.TEXT_LINE ).build() ).build() ).add(
                newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        final ContentType contentType = newContentType().
            addFormItem( newSubTypeReference( subType ).name( "home" ).build() ).
            build();

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        // exercise
        contentType.form().subTypeReferencesToFormItems( subTypeFetcher );

        // verify:
        assertEquals( "home.street", contentType.form().getInput( "home.street" ).getPath().toString() );
        assertEquals( "home.myFieldInLayout", contentType.form().getInput( "home.myFieldInLayout" ).getPath().toString() );
    }


    @Test
    public void subTypeReferencesToFormItems_throws_exception_when_subType_is_not_of_expected_type()
    {
        // setup
        FormItemSetSubType subType = newFormItemSetSubType().module( ModuleName.from( "myModule" ) ).formItemSet(
            newFormItemSet().name( "address" ).add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        final ContentType cty = newContentType().
            addFormItem( newSubTypeReference().name( "home" ).typeInput().subType( subType.getQualifiedName() ).build() ).
            build();

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        // exercise
        try
        {
            cty.form().subTypeReferencesToFormItems( subTypeFetcher );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "SubType expected to be of type InputSubType: FormItemSetSubType", e.getMessage() );
        }
    }

    @Test
    public void formItemSet_in_formItemSet()
    {
        FormItemSet formItemSet =
            newFormItemSet().name( "top-set" ).add( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newFormItemSet().name( "inner-set" ).add(
                    newInput().name( "myInnerInput" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();
        final ContentType contentType = newContentType().
            name( "test" ).
            addFormItem( formItemSet ).
            build();

        assertEquals( "top-set", contentType.form().getFormItemSet( "top-set" ).getPath().toString() );
        assertEquals( "top-set.myInput", contentType.form().getInput( "top-set.myInput" ).getPath().toString() );
        assertEquals( "top-set.inner-set", contentType.form().getFormItemSet( "top-set.inner-set" ).getPath().toString() );
        assertEquals( "top-set.inner-set.myInnerInput",
                      contentType.form().getInput( "top-set.inner-set.myInnerInput" ).getPath().toString() );
    }
}
