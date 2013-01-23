package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.type.DeleteContentTypes;
import com.enonic.wem.api.content.type.ContentTypeDeletionResult;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.exception.ContentTypeNotFoundException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;

public class DeleteContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteContentTypesHandler handler;

    private ContentTypeDao contentTypeDao;

    private ContentDao contentDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        contentDao = Mockito.mock( ContentDao.class );
        handler = new DeleteContentTypesHandler();
        handler.setContentTypeDao( contentTypeDao );
        handler.setContentDao( contentDao );
    }

    @Test
    public void deleteContentType()
        throws Exception
    {
        // exercise
        final QualifiedContentTypeNames names = QualifiedContentTypeNames.from( "my:contentType" );
        final DeleteContentTypes command = Commands.contentType().delete().names( names );
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( contentTypeDao, only() ).delete( isA( QualifiedContentTypeName.class ), any( Session.class ) );

        ContentTypeDeletionResult result = command.getResult();
        assertEquals( false, result.hasFailures() );
        assertEquals( 1, getCount( result.successes() ) );
    }

    @Test
    public void deleteVariousContentTypes()
        throws Exception
    {
        // exercise
        final QualifiedContentTypeName existingName = new QualifiedContentTypeName( "my:existingContentType" );
        final QualifiedContentTypeName notFoundName = new QualifiedContentTypeName( "my:notFoundContentType" );
        final QualifiedContentTypeName beingUsedName = new QualifiedContentTypeName( "my:beingUsedContentType" );

        Mockito.doThrow( new ContentTypeNotFoundException( notFoundName ) ).when( contentTypeDao ).delete( eq( notFoundName ),
                                                                                                           any( Session.class ) );
        Mockito.doReturn( 1 ).when( contentDao ).countContentTypeUsage( eq( beingUsedName ), any( Session.class ) );

        final QualifiedContentTypeNames names = QualifiedContentTypeNames.from( existingName, notFoundName, beingUsedName );
        final DeleteContentTypes command = Commands.contentType().delete().names( names );

        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( contentDao, times( 3 ) ).countContentTypeUsage( isA( QualifiedContentTypeName.class ), any( Session.class ) );
        Mockito.verify( contentTypeDao, times( 2 ) ).delete( isA( QualifiedContentTypeName.class ), any( Session.class ) );

        ContentTypeDeletionResult result = command.getResult();
        assertEquals( true, result.hasFailures() );
        assertEquals( 2, getCount( result.failures() ) );
        assertEquals( 1, getCount( result.successes() ) );
    }


    private int getCount( Iterable names )
    {
        int count = 0;
        for ( final Object name : names )
        {
            count++;
        }
        return count;
    }

}
