package com.enonic.wem.core.schema.mixin.dao;


import javax.jcr.Node;

import org.junit.Test;


import com.enonic.wem.api.schema.content.form.FormItem;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.core.AbstractJcrTest;

import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
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
        Input myInput = newInput().name( "my_input" ).label( "My input" ).inputType( InputTypes.TEXT_LINE ).build();
        Mixin.Builder mixinBuilder = newMixin().name( "my_input" ).
            displayName( "My Mixin" ).addFormItem( myInput );
        Mixin mixin = mixinBuilder.build();

        // exercise
        mixinDao.create( mixin, session );
        commit();

        // verify
        Node mixinNode = session.getNode( "/" + MixinDao.MIXINS_PATH + "my_input" );
        assertNotNull( mixinNode );
    }

    @Test
    public void updateMixin()
        throws Exception
    {
        // setup
        Mixin mixin = newMixin().name( "my_input" ).
            displayName( "My Mixin" ).addFormItem(
            newInput().name( "my_input" ).label( "My input" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        mixinDao.create( mixin, session );

        // exercise
        Mixins mixinsAfterCreate = mixinDao.select( QualifiedMixinNames.from( "my_input" ), session );
        assertNotNull( mixinsAfterCreate );
        assertEquals( 1, mixinsAfterCreate.getSize() );

        Mixin updatedMixin = newMixin().name( "my_input" ).
            displayName( "My Updated Mixin" ).addFormItem(
            newInput().name( "my_input" ).label( "My input" ).inputType( InputTypes.TEXT_AREA ).build() ).build();
        mixinDao.update( updatedMixin, session );
        commit();

        // verify
        final Mixins mixinsAfterUpdate = mixinDao.select( QualifiedMixinNames.from( "my_input" ), session );
        assertNotNull( mixinsAfterUpdate );
        assertEquals( 1, mixinsAfterUpdate.getSize() );
        final Mixin mixin1 = mixinsAfterUpdate.first();
        assertEquals( "my_input", mixin1.getName() );
        assertEquals( "My Updated Mixin", mixin1.getDisplayName() );
        assertEquals( InputTypes.TEXT_AREA, mixin1.getFormItems().iterator().next().toInput().getInputType() );
    }

    @Test
    public void deleteMixin()
        throws Exception
    {
        // setup
        Mixin mixin = newMixin().name( "my_input" ).
            displayName( "My Mixin" ).addFormItem(
            newInput().name( "my_input" ).label( "My input" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        mixinDao.create( mixin, session );

        assertEquals( 1, mixinDao.select( QualifiedMixinNames.from( "my_input" ), session ).getSize() );

        // exercise

        mixinDao.delete( mixin.getQualifiedName(), session );
        commit();

        // verify
        assertEquals( 0, mixinDao.select( QualifiedMixinNames.from( "my_input" ), session ).getSize() );
    }

    @Test
    public void retrieveMixin()
        throws Exception
    {
        // setup
        Mixin mixin = newMixin().name( "my_input" ).
            displayName( "My Mixin" ).addFormItem(
            newInput().name( "my_input" ).label( "My input" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        mixinDao.create( mixin, session );

        // exercise
        final Mixins mixins = mixinDao.select( QualifiedMixinNames.from( "my_input" ), session );
        commit();

        // verify
        assertNotNull( mixins );
        assertEquals( 1, mixins.getSize() );
        Mixin mixin1 = mixins.first();
        FormItem formItem1 = mixin1.getFormItems().iterator().next();
        assertEquals( "my_input", mixin1.getName() );
        assertEquals( "My Mixin", mixin1.getDisplayName() );
        assertEquals( "my_input", formItem1.getName() );
        assertEquals( "My input", formItem1.toInput().getLabel() );
        assertEquals( InputTypes.TEXT_LINE, formItem1.toInput().getInputType() );
    }

    @Test
    public void retrieveAllMixins()
        throws Exception
    {
        // setup
        Mixin mixin1 = newMixin().name( "my_input" ).
            displayName( "My Mixin 1" ).addFormItem(
            newInput().name( "my_input" ).label( "My input 1" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        mixinDao.create( mixin1, session );

        Mixin mixin2 = newMixin().name( "my_input2" ).
            displayName( "My Mixin 2" ).addFormItem(
            newInput().name( "my_input" ).label( "My input 2" ).inputType( InputTypes.DATE ).build() ).build();
        mixinDao.create( mixin2, session );

        // exercise
        final Mixins mixins = mixinDao.selectAll( session );
        commit();

        // verify
        assertNotNull( mixins );
        assertEquals( 2, mixins.getSize() );
        Mixin actualMixin1 = mixins.getMixin( QualifiedMixinName.from( "my_input" ) );
        Mixin actualMixin2 = mixins.getMixin( QualifiedMixinName.from( "my_input2" ) );

        assertEquals( "my_input", actualMixin1.getName() );
        assertEquals( "My Mixin 1", actualMixin1.getDisplayName() );
        assertEquals( InputTypes.TEXT_LINE, actualMixin1.getFormItems().iterator().next().toInput().getInputType() );

        assertEquals( "my_input2", actualMixin2.getName() );
        assertEquals( "My Mixin 2", actualMixin2.getDisplayName() );
        assertEquals( InputTypes.DATE, actualMixin2.getFormItems().iterator().next().toInput().getInputType() );
    }

}
