package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.type.UpdateContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.content.type.editor.ContentTypeEditor;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

import static com.enonic.wem.api.content.type.ContentType.newContentType;
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
        final ContentType contentType = newContentType().
            name( "myContentType" ).
            module( ModuleName.from( "myModule" ) ).
            displayName( "My content type" ).
            setAbstract( false ).
            build();
        final ContentTypes contentTypes = ContentTypes.from( contentType );
        Mockito.when( contentTypeDao.retrieveContentTypes( any( Session.class ), isA( QualifiedContentTypeNames.class ) ) ).thenReturn(
            contentTypes );

        // exercise
        final QualifiedContentTypeNames names = QualifiedContentTypeNames.from( "myModule:myContentType" );
        final UpdateContentTypes command = Commands.contentType().update().names( names ).editor( new ContentTypeEditor()
        {
            @Override
            public ContentType edit( final ContentType contentType )
                throws Exception
            {
                return newContentType( contentType ).
                    displayName( contentType.getDisplayName() + "-updated" ).
                    build();
            }
        } );
        this.handler.handle( this.context, command );

        // verify
        verify( contentTypeDao, atLeastOnce() ).updateContentType( Mockito.any( Session.class ), Mockito.isA( ContentType.class ) );
        assertEquals( (Integer) 1, command.getResult() );
    }

}
