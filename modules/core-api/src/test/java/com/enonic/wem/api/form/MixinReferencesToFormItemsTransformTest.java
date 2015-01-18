package com.enonic.wem.api.form;


import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinService;

import static com.enonic.wem.api.form.Form.newForm;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.MixinReference.newMixinReference;
import static org.junit.Assert.*;

public class MixinReferencesToFormItemsTransformTest
{
    private MixinReferencesToFormItemsTransformer transformer;

    private MixinService mixinService;


    public MixinReferencesToFormItemsTransformTest()
    {
        mixinService = Mockito.mock( MixinService.class );
        transformer = new MixinReferencesToFormItemsTransformer( mixinService );
    }

    @Test
    public void transform_input()
    {
        // setup
        Mixin mixin = Mixin.newMixin().
            name( "mymodule:my_mixin" ).
            addFormItem( newInput().name( "input1" ).
                inputType( InputTypes.TEXT_LINE ).
                helpText( "myHelpText" ).
                build() ).
            build();

        Form form = newForm().
            addFormItem( newInput().name( "my_input" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( newMixinReference().name( "my_mixin" ).mixin( "mymodule:my_mixin" ).build() ).
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
            newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build();

        Form form = newForm().
            addFormItem( newInput().name( "title" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( newMixinReference( mixin ).name( "address" ).build() ).
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
                addFormItem( newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( newInput().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( newInput().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() ).
                build() ).
            build();

        Form form = newForm().
            addFormItem( newFormItemSet().
                name( "home" ).
                addFormItem( MixinReference.newMixinReference( mixin ).name( "home" ).build() ).
                build() ).
            addFormItem( newFormItemSet().
                name( "cottage" ).
                addFormItem( MixinReference.newMixinReference( mixin ).name( "cottage" ).build() ).
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
    public void mixinReferencesToFormItems_layout()
    {
        // setup
        Mixin mixin = Mixin.newMixin().
            name( "mymodule:address" ).
            addFormItem( newFormItemSet().
                name( "address" ).
                addFormItem( FieldSet.newFieldSet().
                    label( "My Field Set" ).
                    name( "fieldSet" ).
                    addFormItem( newInput().
                        name( "myFieldInLayout" ).
                        label( "MyFieldInLayout" ).
                        inputType( InputTypes.TEXT_LINE ).
                        build() ).
                    build() ).
                addFormItem( newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( newInput().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() ).
                addFormItem( newInput().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() ).
                build() ).
            build();

        Form form = newForm().
            addFormItem( MixinReference.newMixinReference( mixin ).name( "home" ).build() ).
            build();

        Mockito.when( mixinService.getByName( Mockito.isA( MixinName.class ) ) ).thenReturn( mixin );

        // exercise
        Form transformedForm = transformer.transformForm( form );

        // verify:
        assertEquals( "address.street", transformedForm.getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.myFieldInLayout", transformedForm.getInput( "address.myFieldInLayout" ).getPath().toString() );
    }

}
