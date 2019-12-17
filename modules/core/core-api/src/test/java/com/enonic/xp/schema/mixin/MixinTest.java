package com.enonic.xp.schema.mixin;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MixinTest
{

    @Test
    public void adding_a_formItemSetMixin_to_another_formItemSetMixin_throws_exception()
    {
        Mixin ageMixin = Mixin.create().name( "myapplication:age" ).addFormItem(
            Input.create().name( "age" ).label( "Age" ).inputType( InputTypeName.TEXT_LINE ).build() ).build();

        final FormItemSet personFormItemSet = FormItemSet.create().name( "person" ).addFormItem(
            Input.create().name( "name" ).label( "Name" ).inputType( InputTypeName.TEXT_LINE ).build() ).addFormItem(
            InlineMixin.create( ageMixin ).build() ).build();

        Mixin addressMixin = Mixin.create().name( MixinName.from( ApplicationKey.from( "myapplication" ), "address" ) ).addFormItem(
            FormItemSet.create().name( "address" ).addFormItem(
                Input.create().inputType( InputTypeName.TEXT_LINE ).name( "street" ).label( "Street" ).build() ).addFormItem(
                Input.create().inputType( InputTypeName.TEXT_LINE ).name( "postalCode" ).label( "Postal code" ).build() ).addFormItem(
                Input.create().inputType( InputTypeName.TEXT_LINE ).name( "postalPlace" ).label(
                    "Postal place" ).build() ).build() ).build();

        try
        {
            personFormItemSet.add( InlineMixin.create( addressMixin ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A Mixin cannot reference other Mixins unless it is of type InputMixin: FormItemSetMixin", e.getMessage() );
        }
    }

    @Test
    public void mixinBuilderTest()
    {
        final Form.Builder formBuilder = Form.create();
        formBuilder.addFormItem( Input.create().name( "name" ).label( "Name" ).inputType( InputTypeName.TEXT_LINE ).build() );

        Mixin mixin1 = Mixin.create().name( MixinName.from( "myapplication:my1" ) ).form( formBuilder.build() ).build();
        Mixin mixin2 = Mixin.create( mixin1 ).build();
        assertEquals( mixin1.getForm(), mixin2.getForm() );
    }

}
