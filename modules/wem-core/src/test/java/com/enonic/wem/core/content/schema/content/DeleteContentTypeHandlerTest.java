package com.enonic.wem.core.content.schema.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.content.DeleteContentType;
import com.enonic.wem.api.command.content.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.exception.ContentTypeNotFoundException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;

public class DeleteContentTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteContentTypeHandler handler;

    private ContentTypeDao contentTypeDao;

    private ContentDao contentDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        contentDao = Mockito.mock( ContentDao.class );
        handler = new DeleteContentTypeHandler();
        handler.setContentTypeDao( contentTypeDao );
        handler.setContentDao( contentDao );
    }

    @Test
    public void deleteContentType()
        throws Exception
    {
        // exercise
        final QualifiedContentTypeName names = new QualifiedContentTypeName( "my:contentType" );
        final DeleteContentType command = Commands.contentType().delete().name( names );
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( contentTypeDao, only() ).delete( isA( QualifiedContentTypeName.class ), any( Session.class ) );

        DeleteContentTypeResult result = command.getResult();
        assertEquals( DeleteContentTypeResult.SUCCESS, result );
    }

    @Test
    public void deleteContentTypeInUse()
        throws Exception
    {
        // exercise
        final QualifiedContentTypeName inUsedName = new QualifiedContentTypeName( "my:inUseContentType" );

        Mockito.doReturn( 1 ).when( contentDao ).countContentTypeUsage( eq( inUsedName ), any( Session.class ) );

        final DeleteContentType command = Commands.contentType().delete().name( inUsedName );

        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( contentTypeDao, never() ).delete( isA( QualifiedContentTypeName.class ), any( Session.class ) );

        DeleteContentTypeResult result = command.getResult();
        assertEquals( DeleteContentTypeResult.UNABLE_TO_DELETE, result );
    }

    @Test
    public void deleteContentTypeNotFound()
        throws Exception
    {
        // exercise
        final QualifiedContentTypeName notFoundName = new QualifiedContentTypeName( "my:notFoundContentType" );

        Mockito.doThrow( new ContentTypeNotFoundException( notFoundName ) ).when( contentTypeDao ).delete( eq( notFoundName ),
                                                                                                           any( Session.class ) );
        final DeleteContentType command = Commands.contentType().delete().name( notFoundName );

        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( contentTypeDao, only() ).delete( isA( QualifiedContentTypeName.class ), any( Session.class ) );

        DeleteContentTypeResult result = command.getResult();
        assertEquals( DeleteContentTypeResult.NOT_FOUND, result );
    }

}
