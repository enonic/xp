package com.enonic.wem.core.schema.mixin;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.GetMixins;

import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class GetMixinsHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetMixinsHandler handler;

    private MixinDao mixinDao;


    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        mixinDao = Mockito.mock( MixinDao.class );

        handler = new GetMixinsHandler();
        handler.setContext( this.context );
        handler.setMixinDao( mixinDao );
    }


    @Test
    public void getMixin()
        throws Exception
    {
        // setup
        final Mixin mixin = newMixin().
            name( "age" ).
            displayName( "Age" ).
            addFormItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        final Mixins mixins = Mixins.from( mixin );
        Mockito.when( mixinDao.select( isA( QualifiedMixinNames.class ), any( Session.class ) ) ).thenReturn( mixins );

        // exercise
        final QualifiedMixinNames names = QualifiedMixinNames.from( "mymodule:like" );
        final GetMixins command = Commands.mixin().get().names( names );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( mixinDao, atLeastOnce() ).select( Mockito.isA( QualifiedMixinNames.class ), Mockito.any( Session.class ) );
        assertEquals( 1, command.getResult().getSize() );
    }

    @Test
    public void getAllMixins()
        throws Exception
    {
        // setup
        final Mixin mixin = newMixin().
            name( "age" ).
            displayName( "Age" ).
            addFormItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        final Mixin mixin2 = newMixin().
            name( "gender" ).
            displayName( "Gender" ).
            addFormItem( newInput().name( "gender" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();
        final Mixins mixins = Mixins.from( mixin, mixin2 );
        Mockito.when( mixinDao.selectAll( any( Session.class ) ) ).thenReturn( mixins );

        // exercise
        final GetMixins command = Commands.mixin().get().all();

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( mixinDao, atLeastOnce() ).selectAll( Mockito.any( Session.class ) );
        assertEquals( 2, command.getResult().getSize() );
    }
}
