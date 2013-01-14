package com.enonic.wem.api.content.type.form;

import org.junit.Test;

import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.Mixin.newMixin;
import static com.enonic.wem.api.content.type.form.MixinReference.newMixinReference;
import static org.junit.Assert.*;

public class MixinTest
{

    @Test
    public void adding_a_formItemSetMixin_to_another_formItemSetMixin_throws_exception()
    {
        ModuleName module = ModuleName.from( "myModule" );

        Mixin ageMixin = newMixin().module( module ).formItem( newInput().name( "age" ).type( InputTypes.TEXT_LINE ).build() ).build();

        final FormItemSet personFormItemSet =
            newFormItemSet().name( "person" ).add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newMixinReference( ageMixin ).name( "age" ).build() ).build();
        Mixin personMixin = newMixin().module( module ).formItem( personFormItemSet ).build();

        Mixin addressMixin = newMixin().module( module ).formItem(
            newFormItemSet().name( "address" ).add( newInput().type( InputTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

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
        ModuleName module = ModuleName.from( "myModule" );
        Input input = newInput().name( "tags" ).label( "Tags" ).type( InputTypes.TEXT_LINE ).multiple( true ).build();
        Mixin inputMixin = Mixin.newMixin().module( module ).formItem( input ).build();
    }

}
