package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.type.GetContentTypeTree;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class GetContentTypeTreeHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetContentTypeTreeHandler handler;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        handler = new GetContentTypeTreeHandler();
        handler.setContentTypeDao( contentTypeDao );
    }

    @Test
    public void getContentTypesTree()
        throws Exception
    {
        // setup
        final ContentType contentType1 = newContentType().
            qualifiedName( QualifiedContentTypeName.unstructured() ).
            displayName( "Some root content type" ).
            build();
        final ContentType contentType2 = newContentType().
            name( "myType" ).
            module( ModuleName.from( "myModule" ) ).
            displayName( "My content type" ).
            superType( contentType1.getQualifiedName() ).
            build();
        final ContentType contentType3 = newContentType().
            name( "subType" ).
            module( ModuleName.from( "myModule" ) ).
            displayName( "My sub-content type" ).
            superType( new QualifiedContentTypeName( "myModule:myType" ) ).
            build();
        final ContentTypes contentTypes = ContentTypes.from( contentType1, contentType2, contentType3 );
        Mockito.when( contentTypeDao.retrieveAllContentTypes( any( Session.class ) ) ).thenReturn( contentTypes );

        // exercise
        final GetContentTypeTree command = Commands.contentType().getTree();
        this.handler.handle( this.context, command );

        // verify
        verify( contentTypeDao, atLeastOnce() ).retrieveAllContentTypes( Mockito.any( Session.class ) );
        final Tree<ContentType> tree = command.getResult();
        assertEquals( 1, tree.size() );
        assertEquals( 3, tree.deepSize() );
        assertEquals( "System:unstructured", tree.getRootNode( 0 ).getObject().getQualifiedName().toString() );
        assertEquals( "myModule:myType", tree.getRootNode( 0 ).getChild( 0 ).getObject().getQualifiedName().toString() );
        assertEquals( "myModule:subType", tree.getRootNode( 0 ).getChild( 0 ).getChild( 0 ).getObject().getQualifiedName().toString() );

    }
}
