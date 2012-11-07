package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeNames;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class GetContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetContentTypesHandler handler;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        handler = new GetContentTypesHandler();
        handler.setContentTypeDao( contentTypeDao );
    }

    @Test
    public void getContentType()
        throws Exception
    {
        // setup
        final ContentType contentType = new ContentType();
        contentType.setName( "myContentType" );
        contentType.setModule( new Module( "myModule" ) );
        contentType.setDisplayName( "My content type" );
        contentType.setAbstract( false );
        final ContentTypes contentTypes = ContentTypes.from( contentType );
        Mockito.when( contentTypeDao.retrieveContentTypes( any( Session.class ), isA( ContentTypeNames.class ) ) ).thenReturn(
            contentTypes );

        // exercise
        final ContentTypeNames names = ContentTypeNames.from( "myModule:myContentType" );
        final GetContentTypes command = Commands.contentType().get().names( names );
        this.handler.handle( this.context, command );

        // verify
        verify( contentTypeDao, atLeastOnce() ).retrieveContentTypes( Mockito.any( Session.class ), Mockito.isA( ContentTypeNames.class ) );
        assertEquals( 1, command.getResult().getSize() );
    }

}
