package com.enonic.wem.core.schema.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetRootContentTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class GetRootContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetRootContentTypesHandler handler;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        handler = new GetRootContentTypesHandler();
        handler.setContext( this.context );
        handler.setContentTypeDao( contentTypeDao );
    }

    @Test
    public void getRootContentTypes()
        throws Exception
    {
        // setup
        final ContentType contentType1 = newContentType().
            name( ContentTypeName.unstructured() ).
            builtIn( true ).
            displayName( "Unstructured root content type" ).
            build();
        final ContentType contentType2 = newContentType().
            name( "my_type" ).
            displayName( "My content type" ).
            superType( contentType1.getQualifiedName() ).
            build();
        final ContentType contentType3 = newContentType().
            name( "sub_type_1" ).
            displayName( "My sub-content-1 type" ).
            superType( contentType2.getQualifiedName() ).
            build();
        final ContentType contentType4 = newContentType().
            name( "sub_type_2" ).
            displayName( "My sub-content-2 type" ).
            superType( contentType2.getQualifiedName() ).
            build();
        final ContentType contentType5 = newContentType().
            name( ContentTypeName.folder() ).
            builtIn( true ).
            displayName( "Folder root content type" ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType1, contentType2, contentType3, contentType4, contentType5 );
        Mockito.when( contentTypeDao.selectAll( Mockito.isA( Session.class ) ) ).thenReturn( contentTypes );

        // exercise
        final GetRootContentTypes command = Commands.contentType().getRoots();
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( contentTypeDao, atLeastOnce() ).selectAll( Mockito.isA( Session.class ) );
        final ContentTypes types = command.getResult();
        assertEquals( 2, types.getSize() );
        assertEquals( "unstructured", types.get( 0 ).getQualifiedName().toString() );
        assertEquals( "folder", types.get( 1 ).getQualifiedName().toString() );

        verify( contentTypeDao, Mockito.only() ).selectAll( Mockito.isA( Session.class ) );

    }
}
