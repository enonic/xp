package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeNames;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.editor.ContentTypeEditor;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class UpdateContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateContentTypesHandler handler;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        handler = new UpdateContentTypesHandler();
        handler.setContentTypeDao( contentTypeDao );
    }

    @Test
    public void updateContentType()
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
        final UpdateContentTypes command = Commands.contentType().update().names( names ).editor( new ContentTypeEditor()
        {
            @Override
            public boolean edit( final ContentType contentType )
                throws Exception
            {
                contentType.setDisplayName( contentType.getDisplayName() + "-updated" );
                return true;
            }
        } );
        this.handler.handle( this.context, command );

        // verify
        verify( contentTypeDao, atLeastOnce() ).updateContentType( Mockito.any( Session.class ), Mockito.isA( ContentType.class ) );
        assertEquals( (Integer) 1, command.getResult() );
    }

}
