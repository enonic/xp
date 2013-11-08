package com.enonic.wem.core.schema.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.DeleteNodeResult;
import com.enonic.wem.api.command.schema.content.DeleteContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class DeleteContentTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteContentTypeHandler handler;

    private ContentDao contentDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();
        contentDao = Mockito.mock( ContentDao.class );
        handler = new DeleteContentTypeHandler();
        handler.setContentDao( contentDao );
        handler.setContext( this.context );
    }

    @Test
    public void deleteContentType()
        throws Exception
    {
        // exercise
        final ContentTypeName names = ContentTypeName.from( "my_content_type" );
        final DeleteContentType command = Commands.contentType().delete().name( names );

        Mockito.when( client.execute( Mockito.isA( DeleteNodeByPath.class ) ) ).thenReturn( DeleteNodeResult.SUCCESS );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( client, Mockito.times( 1 ) ).execute( Mockito.isA( DeleteNodeByPath.class ) );

        DeleteContentTypeResult result = command.getResult();
        assertEquals( DeleteContentTypeResult.SUCCESS, result );
    }

    @Test
    public void deleteContentTypeInUse()
        throws Exception
    {
        // exercise
        final ContentTypeName inUsedName = ContentTypeName.from( "in_use_content_type" );

        Mockito.doReturn( 1 ).when( contentDao ).countContentTypeUsage( eq( inUsedName ), any( Session.class ) );

        final DeleteContentType command = Commands.contentType().delete().name( inUsedName );

        this.handler.setCommand( command );
        this.handler.handle();

        DeleteContentTypeResult result = command.getResult();
        assertEquals( DeleteContentTypeResult.UNABLE_TO_DELETE, result );
    }

    @Test
    public void deleteContentTypeNotFound()
        throws Exception
    {
        // exercise
        final ContentTypeName notFoundName = ContentTypeName.from( "not_found_content_type" );

        Mockito.when( client.execute( Mockito.isA( DeleteNodeByPath.class ) ) ).thenReturn( DeleteNodeResult.NOT_FOUND );

        final DeleteContentType command = Commands.contentType().delete().name( notFoundName );

        this.handler.setCommand( command );
        this.handler.handle();

        DeleteContentTypeResult result = command.getResult();
        assertEquals( DeleteContentTypeResult.NOT_FOUND, result );
    }

}
