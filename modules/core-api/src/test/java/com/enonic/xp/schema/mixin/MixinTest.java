package com.enonic.xp.schema.mixin;

import org.junit.Test;

import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.module.ModuleKey;

import static com.enonic.xp.form.FormItemSet.newFormItemSet;
import static com.enonic.xp.form.InlineMixin.newInlineMixin;
import static com.enonic.xp.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class MixinTest
{

    @Test
    public void adding_a_formItemSetMixin_to_another_formItemSetMixin_throws_exception()
    {
        Mixin ageMixin = newMixin().name( "mymodule:age" ).addFormItem(
            Input.create().name( "age" ).label( "Age" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        final FormItemSet personFormItemSet = newFormItemSet().name( "person" ).addFormItem(
            Input.create().name( "name" ).label( "Name" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInlineMixin( ageMixin ).build() ).build();
        Mixin personMixin = newMixin().name( "mymodule:person" ).addFormItem( personFormItemSet ).build();

        Mixin addressMixin = newMixin().name( MixinName.from(  ModuleKey.from("mymodule"), "address" ) ).addFormItem(
            newFormItemSet().name( "address" ).addFormItem(
                Input.create().inputType( InputTypes.TEXT_LINE ).name( "street" ).label( "Street" ).build() ).addFormItem(
                Input.create().inputType( InputTypes.TEXT_LINE ).name( "postalCode" ).label( "Postal code" ).build() ).addFormItem(
                Input.create().inputType( InputTypes.TEXT_LINE ).name( "postalPlace" ).label( "Postal place" ).build() ).build() ).build();

        try
        {
            personFormItemSet.add( newInlineMixin( addressMixin ).build() );
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
        FormItems formItems = new FormItems(  );
        formItems.add( Input.create().name( "name" ).label( "Name" ).inputType( InputTypes.TEXT_LINE ).build() );
        Mixin mixin1 = newMixin().name( MixinName.from("mymodule:my1") ).formItems( formItems ).build();
        Mixin mixin2 = newMixin( mixin1 ).build();
        assertEquals( mixin1.getFormItems(), mixin2.getFormItems() );
    }

}
