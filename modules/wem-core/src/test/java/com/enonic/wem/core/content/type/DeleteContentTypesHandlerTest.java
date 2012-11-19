package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.type.DeleteContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class DeleteContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteContentTypesHandler handler;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        handler = new DeleteContentTypesHandler();
        handler.setContentTypeDao( contentTypeDao );
    }

    @Test
    public void deleteContentType()
        throws Exception
    {
        // setup
        Mockito.when( contentTypeDao.deleteContentType( any( Session.class ), isA( QualifiedContentTypeNames.class ) ) ).thenReturn( 1 );

        // exercise
        final QualifiedContentTypeNames names = QualifiedContentTypeNames.from( "myModule:myContentType" );
        final DeleteContentTypes command = Commands.contentType().delete().names( names );
        this.handler.handle( this.context, command );

        // verify
        verify( contentTypeDao, atLeastOnce() ).deleteContentType( Mockito.any( Session.class ), Mockito.isA( QualifiedContentTypeNames.class ) );
        assertEquals( (Integer) 1, command.getResult() );
    }

}
