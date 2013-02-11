package com.enonic.wem.core.content.mixin;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Iterables;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.mixin.DeleteMixins;
import com.enonic.wem.api.content.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.mixin.QualifiedMixinNames;
import com.enonic.wem.api.content.type.MixinDeletionResult;
import com.enonic.wem.api.exception.MixinNotFoundException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.mixin.dao.MixinDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;

public class DeleteMixinsHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteMixinsHandler handler;

    private MixinDao mixinDao;


    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        mixinDao = Mockito.mock( MixinDao.class );

        handler = new DeleteMixinsHandler();
        handler.setMixinDao( mixinDao );
    }


    @Test
    public void deleteSingleMixin()
        throws Exception
    {
        // exercise
        final QualifiedMixinNames names = QualifiedMixinNames.from( "my:mixin" );
        final DeleteMixins command = Commands.mixin().delete().names( names );
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( mixinDao, only() ).delete( isA( QualifiedMixinName.class ), any( Session.class ) );

        MixinDeletionResult result = command.getResult();
        assertEquals( false, result.hasFailures() );
        assertEquals( 1, Iterables.size( result.successes() ) );
    }

    @Test
    public void deleteMultipleMixins()
        throws Exception
    {
        // exercise
        final QualifiedMixinName existingName = QualifiedMixinName.from( "my:existingMixin" );
        final QualifiedMixinName anotherExistingName = QualifiedMixinName.from( "my:anotherMixin" );
        final QualifiedMixinName notFoundName = QualifiedMixinName.from( "my:notFoundMixin" );

        Mockito.doThrow( new MixinNotFoundException( notFoundName ) ).
            when( mixinDao ).delete( eq( notFoundName ), any( Session.class ) );

        final QualifiedMixinNames names = QualifiedMixinNames.from( existingName, notFoundName, anotherExistingName );
        final DeleteMixins command = Commands.mixin().delete().names( names );

        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( mixinDao, times( 3 ) ).delete( isA( QualifiedMixinName.class ), any( Session.class ) );

        MixinDeletionResult result = command.getResult();
        assertEquals( true, result.hasFailures() );
        assertEquals( 1, Iterables.size( result.failures() ) );
        assertEquals( 2, Iterables.size( result.successes() ) );
    }
}
