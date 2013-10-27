package com.enonic.wem.core.schema.mixin;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

// TODO: Make test work again when MixinHandler is using Node API instead of NodeDao
public class GetMixinsHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetMixinsHandler handler;

    private NodeJcrDao nodeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        nodeDao = Mockito.mock( NodeJcrDao.class );

        handler = new GetMixinsHandler();
        handler.setContext( this.context );
        handler.setNodeJcrDao( nodeDao );
    }

    @Ignore
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
        //Mockito.when( nodeDao.select( isA( QualifiedMixinNames.class ), any( Session.class ) ) ).thenReturn( mixins );

        // exercise
        final QualifiedMixinNames names = QualifiedMixinNames.from( "mymodule:like" );
        final GetMixins command = Commands.mixin().get().byQualifiedNames( names );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        //verify( nodeDao, atLeastOnce() ).select( Mockito.isA( QualifiedMixinNames.class ), Mockito.any( Session.class ) );
        assertEquals( 1, command.getResult().getSize() );
    }

    @Ignore
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
        //Mockito.when( nodeDao.selectAll( any( Session.class ) ) ).thenReturn( mixins );

        // exercise
        final GetMixins command = Commands.mixin().get().all();

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        //verify( nodeDao, atLeastOnce() ).selectAll( Mockito.any( Session.class ) );
        assertEquals( 2, command.getResult().getSize() );
    }
}
