package com.enonic.xp.schema.mixin;

import org.junit.Test;

import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.module.ModuleKey;

import static org.junit.Assert.*;

public class MixinTest
{

    @Test
    public void adding_a_formItemSetMixin_to_another_formItemSetMixin_throws_exception()
    {
        Mixin ageMixin = Mixin.create().name( "mymodule:age" ).addFormItem(
            Input.create().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        final FormItemSet personFormItemSet = FormItemSet.create().name( "person" ).addFormItem(
            Input.create().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            InlineMixin.create( ageMixin ).build() ).build();
        Mixin personMixin = Mixin.create().name( "mymodule:person" ).addFormItem( personFormItemSet ).build();

        Mixin addressMixin = Mixin.create().name( MixinName.from( ModuleKey.from( "mymodule" ), "address" ) ).addFormItem(
            FormItemSet.create().name( "address" ).addFormItem(
                Input.create().inputType( InputTypes.TEXT_LINE ).name( "street" ).build() ).addFormItem(
                Input.create().inputType( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).addFormItem(
                Input.create().inputType( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

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
        FormItems formItems = new FormItems(  );
        formItems.add( Input.create().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() );
        Mixin mixin1 = Mixin.create().name( MixinName.from( "mymodule:my1" ) ).formItems( formItems ).build();
        Mixin mixin2 = Mixin.create( mixin1 ).build();
        assertEquals( mixin1.getFormItems(), mixin2.getFormItems() );
    }

}
