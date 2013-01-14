package com.enonic.wem.core.content.type.dao;


import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.content.QualifiedMixinNames;
import com.enonic.wem.api.content.type.Mixins;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.itest.AbstractJcrTest;

import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.Mixin.newMixin;
import static org.junit.Assert.*;

public class MixinDaoImplTest
    extends AbstractJcrTest
{
    private MixinDao mixinDao;

    public void setupDao()
        throws Exception
    {
        mixinDao = new MixinDaoImpl();
    }

    @Test
    public void createMixin()
        throws Exception
    {
        // setup
        Input myInput = newInput().name( "myInput" ).label( "My input" ).type( InputTypes.TEXT_LINE ).build();
        Mixin.Builder mixinBuilder = newMixin().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My Mixin" ).formItem( myInput );
        Mixin mixin = mixinBuilder.build();

        // exercise
        mixinDao.createMixin( mixin, session );
        commit();

        // verify
        Node mixinNode = session.getNode( "/" + MixinDao.MIXINS_PATH + "myModule/myInput" );
        assertNotNull( mixinNode );
    }

    @Test
    public void updateMixin()
        throws Exception
    {
        // setup
        Mixin mixin = newMixin().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My Mixin" ).formItem(
            newInput().name( "myInput" ).label( "My input" ).type( InputTypes.TEXT_LINE ).build() ).build();
        mixinDao.createMixin( mixin, session );

        // exercise
        Mixins mixinsAfterCreate = mixinDao.retrieveMixins( QualifiedMixinNames.from( "myModule:myInput" ), session );
        assertNotNull( mixinsAfterCreate );
        assertEquals( 1, mixinsAfterCreate.getSize() );

        Mixin updatedMixin = newMixin().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My Updated Mixin" ).formItem(
            newInput().name( "myInput" ).label( "My input" ).type( InputTypes.TEXT_AREA ).build() ).build();
        mixinDao.updateMixin( updatedMixin, session );
        commit();

        // verify
        final Mixins mixinsAfterUpdate = mixinDao.retrieveMixins( QualifiedMixinNames.from( "myModule:myInput" ), session );
        assertNotNull( mixinsAfterUpdate );
        assertEquals( 1, mixinsAfterUpdate.getSize() );
        final Mixin mixin1 = mixinsAfterUpdate.first();
        assertEquals( "myInput", mixin1.getName() );
        assertEquals( "myModule", mixin1.getModuleName().toString() );
        assertEquals( "My Updated Mixin", mixin1.getDisplayName() );
        assertEquals( InputTypes.TEXT_AREA, mixin1.getFormItem().toInput().getInputType() );
    }

    @Test
    public void deleteMixin()
        throws Exception
    {
        // setup
        Mixin mixin = newMixin().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My Mixin" ).formItem(
            newInput().name( "myInput" ).label( "My input" ).type( InputTypes.TEXT_LINE ).build() ).build();
        mixinDao.createMixin( mixin, session );

        assertEquals( 1, mixinDao.retrieveMixins( QualifiedMixinNames.from( "myModule:myInput" ), session ).getSize() );

        // exercise

        mixinDao.deleteMixin( mixin.getQualifiedName(), session );
        commit();

        // verify
        assertEquals( 0, mixinDao.retrieveMixins( QualifiedMixinNames.from( "myModule:myInput" ), session ).getSize() );
    }

    @Test
    public void retrieveMixin()
        throws Exception
    {
        // setup
        Mixin mixin = newMixin().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My Mixin" ).formItem(
            newInput().name( "myInput" ).label( "My input" ).type( InputTypes.TEXT_LINE ).build() ).build();

        mixinDao.createMixin( mixin, session );

        // exercise
        final Mixins mixins = mixinDao.retrieveMixins( QualifiedMixinNames.from( "myModule:myInput" ), session );
        commit();

        // verify
        assertNotNull( mixins );
        assertEquals( 1, mixins.getSize() );
        Mixin mixin1 = (Mixin) mixins.first();
        assertEquals( "myInput", mixin1.getName() );
        assertEquals( "myModule", mixin1.getModuleName().toString() );
        assertEquals( "My Mixin", mixin1.getDisplayName() );
        assertEquals( "myInput", mixin1.getFormItem().getName() );
        assertEquals( "My input", mixin1.getFormItem().toInput().getLabel() );
        assertEquals( InputTypes.TEXT_LINE, mixin1.getFormItem().toInput().getInputType() );
    }

    @Test
    public void retrieveAllMixins()
        throws Exception
    {
        // setup
        Mixin mixin1 = newMixin().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My Mixin 1" ).formItem(
            newInput().name( "myInput" ).label( "My input 1" ).type( InputTypes.TEXT_LINE ).build() ).build();
        mixinDao.createMixin( mixin1, session );

        Mixin mixin2 = newMixin().
            module( ModuleName.from( "otherModule" ) ).
            displayName( "My Mixin 2" ).formItem(
            newInput().name( "myInput" ).label( "My input 2" ).type( InputTypes.DATE ).build() ).build();
        mixinDao.createMixin( mixin2, session );

        // exercise
        final Mixins mixins = mixinDao.retrieveAllMixins( session );
        commit();

        // verify
        assertNotNull( mixins );
        assertEquals( 2, mixins.getSize() );
        Mixin actualMixin1 = mixins.getMixin( new QualifiedMixinName( "myModule:myInput" ) );
        Mixin actualMixin2 = mixins.getMixin( new QualifiedMixinName( "otherModule:myInput" ) );

        assertEquals( "myInput", actualMixin1.getName() );
        assertEquals( "myModule", actualMixin1.getModuleName().toString() );
        assertEquals( "My Mixin 1", actualMixin1.getDisplayName() );
        assertEquals( InputTypes.TEXT_LINE, actualMixin1.getFormItem().toInput().getInputType() );

        assertEquals( "myInput", actualMixin2.getName() );
        assertEquals( "otherModule", actualMixin2.getModuleName().toString() );
        assertEquals( "My Mixin 2", actualMixin2.getDisplayName() );
        assertEquals( InputTypes.DATE, actualMixin2.getFormItem().toInput().getInputType() );
    }

}
