package com.enonic.wem.core.schema.mixin;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.form.FormItem;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.schema.mixin.editor.SetMixinEditor;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class UpdateMixinHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateMixinHandler handler;

    private MixinDao mixinDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        mixinDao = Mockito.mock( MixinDao.class );

        handler = new UpdateMixinHandler();
        handler.setMixinDao( mixinDao );
    }

    @Test
    public void updateMixin()
        throws Exception
    {
        // setup
        final ModuleName module = ModuleName.from( "mymodule" );
        final Mixin existingMixin = newMixin().
            name( "age" ).
            displayName( "Age" ).
            module( module ).
            addFormItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        Mockito.when(
            mixinDao.select( Mockito.eq( QualifiedMixinNames.from( "mymodule:age" ) ), Mockito.any( Session.class ) ) ).thenReturn(
            Mixins.from( existingMixin ) );

        final Mixins mixins = Mixins.from( existingMixin );
        Mockito.when( mixinDao.select( isA( QualifiedMixinNames.class ), any( Session.class ) ) ).thenReturn( mixins );

        final FormItem formItemToSet = newInput().name( "age" ).inputType( InputTypes.WHOLE_NUMBER ).build();
        final UpdateMixin command = Commands.mixin().update().
            qualifiedName( QualifiedMixinName.from( "mymodule:age" ) ).editor( SetMixinEditor.newSetMixinEditor().
            displayName( "age2" ).
            addFormItem( formItemToSet ).build() );

        // exercise
        this.handler.handle( this.context, command );

        // verify
        verify( mixinDao, atLeastOnce() ).update( Mockito.isA( Mixin.class ), Mockito.any( Session.class ) );
        assertEquals( UpdateMixinResult.SUCCESS, command.getResult() );
    }

    @Test
    public void updateMixinNotFound()
        throws Exception
    {
        // setup
        final Mixins mixins = Mixins.empty();
        Mockito.when( mixinDao.select( isA( QualifiedMixinNames.class ), any( Session.class ) ) ).thenReturn( mixins );

        final FormItem formItemToSet = newInput().name( "age" ).inputType( InputTypes.WHOLE_NUMBER ).build();
        final UpdateMixin command = Commands.mixin().update().
            qualifiedName( QualifiedMixinName.from( "mymodule:age" ) ).editor( SetMixinEditor.newSetMixinEditor().
            displayName( "age2" ).
            addFormItem( formItemToSet ).build() );

        // exercise
        this.handler.handle( this.context, command );

        // verify
        verify( mixinDao, never() ).update( Mockito.isA( Mixin.class ), Mockito.any( Session.class ) );
        assertEquals( UpdateMixinResult.NOT_FOUND, command.getResult() );
    }
}
