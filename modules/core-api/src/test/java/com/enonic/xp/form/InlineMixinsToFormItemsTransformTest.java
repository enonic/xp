package com.enonic.xp.form;


import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;

import static com.enonic.xp.form.Form.newForm;
import static com.enonic.xp.form.FormItemSet.newFormItemSet;
import static com.enonic.xp.form.InlineMixin.newInlineMixin;
import static org.junit.Assert.*;

public class InlineMixinsToFormItemsTransformTest
{
    private InlineMixinsToFormItemsTransformer transformer;

    private MixinService mixinService;


    public InlineMixinsToFormItemsTransformTest()
    {
        mixinService = Mockito.mock( MixinService.class );
        transformer = new InlineMixinsToFormItemsTransformer( mixinService );
    }

    @Test
    public void transform_input()
    {
        // setup
        Mixin mixin = Mixin.newMixin().
            name( "mymodule:my_mixin" ).
            addFormItem( Input.create().name( "input1" ).
                inputType( InputTypes.TEXT_LINE ).
                helpText( "myHelpText" ).
                build() ).
            build();

        Form form = newForm().
            addFormItem( Input.create().name( "my_input" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( InlineMixin.newInlineMixin().mixin( "mymodule:my_mixin" ).build() ).
            build();

        Mockito.when( mixinService.getByName( Mockito.isA( MixinName.class ) ) ).thenReturn( mixin );

        // exercise
        Form transformedForm = transformer.transformForm( form );

        // verify:
        final Input mixedInInput = transformedForm.getInput( "input1" );
        assertNotNull( mixedInInput );
        assertEquals( "input1", mixedInInput.getPath().toString() );
        assertEquals( InputTypes.TEXT_LINE, mixedInInput.getInputType() );
        assertEquals( "myHelpText", mixedInInput.getHelpText() );
    }

    @Test
    public void transform_formItemSet()
    {
        // setup
        Mixin mixin = Mixin.newMixin().name( "mymodule:address" ).addFormItem( newFormItemSet().name( "address" ).addFormItem(
            Input.create().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            Input.create().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            Input.create().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            Input.create().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build();

        Form form = newForm().
            addFormItem( Input.create().name( "title" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( newInlineMixin( mixin ).build() ).
            build();

        Mockito.when( mixinService.getByName( Mockito.isA( MixinName.class ) ) ).thenReturn( mixin );

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
        Mixin mixin = Mixin.newMixin().name( "mymodule:address" ).
            addFormItem( newFormItemSet().name( "address" ).
                addFormItem( Input.create().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( Input.create().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( Input.create().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( Input.create().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() ).
                build() ).
            build();

        Form form = newForm().
            addFormItem( newFormItemSet().
                name( "home" ).
                addFormItem( InlineMixin.newInlineMixin( mixin ).build() ).
                build() ).
            addFormItem( newFormItemSet().
                name( "cottage" ).
                addFormItem( InlineMixin.newInlineMixin( mixin ).build() ).
                build() ).
            build();

        Mockito.when( mixinService.getByName( Mockito.isA( MixinName.class ) ) ).thenReturn( mixin );

        // exercise
        Form transformedForm = transformer.transformForm( form );

        // verify:

        assertNotNull( transformedForm.getFormItemSet( "home" ) );
        assertNotNull( transformedForm.getFormItemSet( "cottage" ) );
        assertNotNull( transformedForm.getFormItemSet( "home.address" ) );
        assertNotNull( transformedForm.getFormItemSet( "cottage.address" ) );
        assertEquals( "home.address.street", transformedForm.getInput( "home.address.street" ).getPath().toString() );
        assertEquals( "home.address.postalNo", transformedForm.getInput( "home.address.postalNo" ).getPath().toString() );
        assertEquals( "home.address.country", transformedForm.getInput( "home.address.country" ).getPath().toString() );
        assertEquals( InputTypes.TEXT_LINE, transformedForm.getInput( "home.address.street" ).getInputType() );
        assertEquals( "cottage.address.street", transformedForm.getInput( "cottage.address.street" ).getPath().toString() );
        assertEquals( InputTypes.TEXT_LINE, transformedForm.getInput( "cottage.address.street" ).getInputType() );
    }

    @Test
    public void inlineMixinsToFormItems_layout()
    {
        // setup
        Mixin mixin = Mixin.newMixin().
            name( "mymodule:address" ).
            addFormItem( newFormItemSet().
                name( "address" ).
                addFormItem( FieldSet.newFieldSet().
                    label( "My Field Set" ).
                    name( "fieldSet" ).
                    addFormItem( Input.create().
                        name( "myFieldInLayout" ).
                        label( "MyFieldInLayout" ).
                        inputType( InputTypes.TEXT_LINE ).
                        build() ).
                    build() ).
                addFormItem( Input.create().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( Input.create().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( Input.create().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( Input.create().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() ).
                build() ).
            build();

        Form form = newForm().
            addFormItem( InlineMixin.newInlineMixin( mixin ).build() ).
            build();

        Mockito.when( mixinService.getByName( Mockito.isA( MixinName.class ) ) ).thenReturn( mixin );

        // exercise
        Form transformedForm = transformer.transformForm( form );

        // verify:
        assertEquals( "address.street", transformedForm.getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.myFieldInLayout", transformedForm.getInput( "address.myFieldInLayout" ).getPath().toString() );
    }

}
