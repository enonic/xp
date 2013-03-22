package com.enonic.wem.api.content.schema.content.form;


import org.junit.Test;

import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.MockMixinFetcher;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.schema.content.form.Form.newForm;
import static com.enonic.wem.api.content.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static com.enonic.wem.api.content.schema.content.form.MixinReference.newMixinReference;
import static org.junit.Assert.*;

public class MixinReferencesToFormItemsTransformerTest
{

    private MockMixinFetcher mixinFetcher = new MockMixinFetcher();

    private MixinReferencesToFormItemsTransformer transformer = new MixinReferencesToFormItemsTransformer( mixinFetcher );

    @Test
    public void transform_input()
    {
        // setup
        Mixin mixin = Mixin.newMixin().module( ModuleName.from( "myModule" ) ).formItem(
            newInput().name( "personalNumber" ).inputType( InputTypes.TEXT_LINE ).helpText(
                "Type in your personal number" ).build() ).build();

        Form form = newForm().
            addFormItem( newInput().name( "title" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( newMixinReference( mixin ).name( "personalNo" ).build() ).
            build();

        mixinFetcher.add( mixin );

        // exercise
        Form transformedForm = transformer.transformForm( form );

        // verify:
        assertEquals( "personalNo", transformedForm.getInput( "personalNo" ).getPath().toString() );
        assertEquals( InputTypes.TEXT_LINE, transformedForm.getInput( "personalNo" ).getInputType() );
        assertEquals( "Type in your personal number", transformedForm.getInput( "personalNo" ).getHelpText() );
    }

    @Test
    public void transform_formItemSet()
    {
        // setup
        Mixin mixin = Mixin.newMixin().module( ModuleName.from( "myModule" ) ).formItem( newFormItemSet().name( "address" ).addFormItem(
            newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build();

        Form form = newForm().
            addFormItem( newInput().name( "title" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( newMixinReference( mixin ).name( "address" ).build() ).
            build();

        mixinFetcher.add( mixin );

        // exercise
        Form transformedForm = transformer.transformForm( form );

        // verify:
        assertEquals( "address.label", transformedForm.getInput( "address.label" ).getPath().toString() );
        assertEquals( "address.street", transformedForm.getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.postalNo", transformedForm.getInput( "address.postalNo" ).getPath().toString() );
        assertEquals( "address.country", transformedForm.getInput( "address.country" ).getPath().toString() );
    }

    @Test
    public void transform_two_formItemSets_with_changed_names()
    {
        // setup
        Mixin mixin = Mixin.newMixin().module( ModuleName.from( "myModule" ) ).formItem( newFormItemSet().name( "address" ).addFormItem(
            newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build();

        Form form = newForm().
            addFormItem( MixinReference.newMixinReference( mixin ).name( "home" ).build() ).
            addFormItem( MixinReference.newMixinReference( mixin ).name( "cabin" ).build() ).
            build();

        mixinFetcher.add( mixin );

        // exercise
        Form transformedForm = transformer.transformForm( form );

        // verify:
        assertEquals( "home.street", transformedForm.getInput( "home.street" ).getPath().toString() );
        assertEquals( InputTypes.TEXT_LINE, transformedForm.getInput( "home.street" ).getInputType() );
        assertEquals( "cabin.street", transformedForm.getInput( "cabin.street" ).getPath().toString() );
        assertEquals( InputTypes.TEXT_LINE, transformedForm.getInput( "cabin.street" ).getInputType() );
    }

    @Test
    public void mixinReferencesToFormItems_layout()
    {
        // setup
        Mixin mixin = Mixin.newMixin().module( ModuleName.from( "myModule" ) ).formItem( newFormItemSet().name( "address" ).addFormItem(
            FieldSet.newFieldSet().label( "My Field Set" ).name( "fieldSet" ).add(
                newInput().name( "myFieldInLayout" ).label( "MyFieldInLayout" ).inputType(
                    InputTypes.TEXT_LINE ).build() ).build() ).addFormItem(
            newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build();

        Form form = newForm().
            addFormItem( MixinReference.newMixinReference( mixin ).name( "home" ).build() ).
            build();

        mixinFetcher.add( mixin );

        // exercise
        Form transformedForm = transformer.transformForm( form );

        // verify:
        assertEquals( "home.street", transformedForm.getInput( "home.street" ).getPath().toString() );
        assertEquals( "home.myFieldInLayout", transformedForm.getInput( "home.myFieldInLayout" ).getPath().toString() );
    }

    @Test
    public void mixinReferencesToFormItems_throws_exception_when_mixin_is_not_of_expected_type()
    {
        // setup
        Mixin mixin = Mixin.newMixin().module( ModuleName.from( "myModule" ) ).formItem( newFormItemSet().name( "address" ).addFormItem(
            newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build();

        Form form = newForm().
            addFormItem( newMixinReference().name( "home" ).typeInput().mixin( mixin.getQualifiedName() ).build() ).
            build();

        MockMixinFetcher mixinFetcher = new MockMixinFetcher();
        mixinFetcher.add( mixin );

        // exercise
        try
        {
            transformer.transformForm( form );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Mixin expected to be of type Input: FormItemSet", e.getMessage() );
        }
    }
}
