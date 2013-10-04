package com.enonic.wem.core.schema.mixin;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.exception.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;

public class DeleteMixinHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteMixinHandler handler;

    private MixinDao mixinDao;


    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        mixinDao = Mockito.mock( MixinDao.class );

        handler = new DeleteMixinHandler();
        handler.setContext( this.context );
        handler.setMixinDao( mixinDao );
    }


    @Test
    public void deleteSingleMixin()
        throws Exception
    {
        // exercise
        final QualifiedMixinName name = QualifiedMixinName.from( "my:mixin" );
        final DeleteMixin command = Commands.mixin().delete().name( name );
        this.handler.handle( command );

        // verify
        Mockito.verify( mixinDao, only() ).delete( isA( QualifiedMixinName.class ), any( Session.class ) );

        final DeleteMixinResult result = command.getResult();
        assertEquals( DeleteMixinResult.SUCCESS, result );
    }

    @Test
    public void deleteMissingMixin()
        throws Exception
    {
        // exercise
        final QualifiedMixinName notFoundName = QualifiedMixinName.from( "my:not_found_mixin" );

        Mockito.doThrow( new MixinNotFoundException( notFoundName ) ).
            when( mixinDao ).delete( eq( notFoundName ), any( Session.class ) );

        final DeleteMixin command = Commands.mixin().delete().name( notFoundName );

        this.handler.handle( command );

        // verify
        Mockito.verify( mixinDao, times( 1 ) ).delete( isA( QualifiedMixinName.class ), any( Session.class ) );

        DeleteMixinResult result = command.getResult();
        assertEquals( DeleteMixinResult.NOT_FOUND, result );
    }
}
