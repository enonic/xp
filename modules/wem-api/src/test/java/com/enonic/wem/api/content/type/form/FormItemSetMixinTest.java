package com.enonic.wem.api.content.type.form;

import org.junit.Test;

import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.FormItemSetMixin.newFormItemSetMixin;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.InputMixin.newInputMixin;
import static com.enonic.wem.api.content.type.form.MixinReference.newMixinReference;
import static org.junit.Assert.*;

public class FormItemSetMixinTest
{

    @Test
    public void adding_a_formItemSetMixin_to_another_formItemSetMixin_throws_exception()
    {
        ModuleName module = ModuleName.from( "myModule" );

        InputMixin ageMixin =
            newInputMixin().module( module ).input( newInput().name( "age" ).type( InputTypes.TEXT_LINE ).build() ).build();

        FormItemSetMixin personMixin = newFormItemSetMixin().module( module ).formItemSet(
            newFormItemSet().name( "person" ).add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newMixinReference( ageMixin ).name( "age" ).build() ).build() ).build();

        FormItemSetMixin addressMixin = newFormItemSetMixin().module( module ).formItemSet(
            newFormItemSet().name( "address" ).add( newInput().type( InputTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

        try
        {
            personMixin.addFormItem( newMixinReference( addressMixin ).name( "address" ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A Mixin cannot reference other Mixins unless it is of type InputMixin: FormItemSetMixin", e.getMessage() );
        }
    }

}
