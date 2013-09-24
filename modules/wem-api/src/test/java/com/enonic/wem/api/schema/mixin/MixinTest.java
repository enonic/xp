package com.enonic.wem.api.schema.mixin;

import org.junit.Test;

import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;

import static com.enonic.wem.api.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class MixinTest
{

    @Test
    public void adding_a_formItemSetMixin_to_another_formItemSetMixin_throws_exception()
    {
        ModuleName module = ModuleName.from( "mymodule" );

        Mixin ageMixin = newMixin().name( "age" ).module( module ).addFormItem(
            newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        final FormItemSet personFormItemSet = newFormItemSet().name( "person" ).addFormItem(
            newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newMixinReference( ageMixin ).name( "age" ).build() ).build();
        Mixin personMixin = newMixin().name( "person" ).module( module ).addFormItem( personFormItemSet ).build();

        Mixin addressMixin = newMixin().name( "address" ).module( module ).addFormItem( newFormItemSet().name( "address" ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "street" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

        try
        {
            personFormItemSet.add( newMixinReference( addressMixin ).name( "address" ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A Mixin cannot reference other Mixins unless it is of type InputMixin: FormItemSetMixin", e.getMessage() );
        }
    }

    @Test
    public void tags()
    {
        ModuleName module = ModuleName.from( "mymodule" );
        Input input = newInput().name( "tags" ).label( "Tags" ).inputType( InputTypes.TEXT_LINE ).multiple( true ).build();
        Mixin inputMixin = Mixin.newMixin().name( "tags" ).module( module ).addFormItem( input ).build();
    }

}
