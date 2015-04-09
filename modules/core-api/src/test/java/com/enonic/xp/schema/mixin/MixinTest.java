package com.enonic.xp.schema.mixin;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.mixin.Mixin;

import static com.enonic.xp.form.FormItemSet.newFormItemSet;
import static com.enonic.xp.form.InlineMixin.newInlineMixin;
import static com.enonic.xp.form.Input.newInput;
import static com.enonic.xp.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class MixinTest
{

    @Test
    public void adding_a_formItemSetMixin_to_another_formItemSetMixin_throws_exception()
    {
        Mixin ageMixin = newMixin().name( "mymodule:age" ).addFormItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        final FormItemSet personFormItemSet = newFormItemSet().name( "person" ).addFormItem(
            newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInlineMixin( ageMixin ).build() ).build();
        Mixin personMixin = newMixin().name( "mymodule:person" ).addFormItem( personFormItemSet ).build();

        Mixin addressMixin = newMixin().name( MixinName.from(  ModuleKey.from("mymodule"), "address" ) ).addFormItem(
            newFormItemSet().name( "address" ).addFormItem(
                newInput().inputType( InputTypes.TEXT_LINE ).name( "street" ).build() ).addFormItem(
                newInput().inputType( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).addFormItem(
                newInput().inputType( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

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
        formItems.add( newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() );
        Mixin mixin1 = newMixin().name( MixinName.from("mymodule:my1") ).formItems( formItems ).build();
        Mixin mixin2 = newMixin( mixin1 ).build();
        assertEquals( mixin1.getFormItems(), mixin2.getFormItems() );
    }

    @Test
    public void test_immutable_MixinNames()
    {
        List<MixinName> names = Lists.newArrayList();
        MixinName mixinName = MixinName.from( "mymodule:my" );
        MixinNames mixinNames = MixinNames.from( names );
        try{
            mixinNames.getList().add( MixinName.from( "mymodule:my1" ) );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
        mixinNames = MixinNames.from( MixinName.from( "mymodule:my1" ) );
        try{
            mixinNames.getList().add( mixinName );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
        mixinNames = MixinNames.from( "mymodule:my1" );
        try{
            mixinNames.getList().add( mixinName );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
    }

    @Test
    public void test_immutable_mixins()
    {
        MixinName mixinName = MixinName.from( "mymodule:my1" );
        Mixin mixin = newMixin().name( mixinName ).build();
        Mixins mixins = Mixins.from( mixin );

        assertTrue( mixins.getNames().size() == 1 );
        assertNotNull( mixins.getMixin( mixinName ) );

        try{
            mixins.getList().add( null );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }
        mixins = Mixins.from( new ArrayList<Mixin>(){{add(mixin);}} );
        try{
            mixins.getList().add( null );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UnsupportedOperationException );
        }

        mixins = Mixins.newMixins().add(mixin).build();
        assertTrue( mixins.getNames().size() == 1 );
        mixins = Mixins.empty();
        assertTrue( mixins.getNames().size() == 0 );
    }

}
