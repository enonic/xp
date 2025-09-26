package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetContentByIdAndVersionIdCommandTest
{

    private final ContentId contentId = ContentId.from( "contentId" );

    private final ContentVersionId versionId = ContentVersionId.from( "versionId" );

    private final Node node = Node.create().build();

    private NodeService nodeService;

    private ContentNodeTranslator translator;

    private ContentTypeService contentTypeService;

    private EventPublisher eventPublisher;

    @BeforeEach
    public void setUp()
    {
        nodeService = Mockito.mock( NodeService.class );
        translator = Mockito.mock( ContentNodeTranslator.class );
        contentTypeService = Mockito.mock( ContentTypeService.class );
        eventPublisher = Mockito.mock( EventPublisher.class );
    }

    @Test
    public void testExecute()
    {
        final PropertyTree contentData = new PropertyTree();
        contentData.addString( "property", "value" );

        final Content content = Content.create().
            name( "name" ).
            parentPath( ContentPath.ROOT ).
            data( contentData ).
            build();

        when( nodeService.getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) ) ).thenReturn( node );
        when( translator.fromNodeWithAnyRootPath( any( Node.class ) ) ).thenReturn( content );

        final Content result = createInstance().execute();

        assertNotNull( result );
        assertEquals( content, result );

        verify( nodeService, times( 1 ) ).getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) );
        verify( translator, times( 1 ) ).fromNodeWithAnyRootPath( any( Node.class ) );
        verifyNoMoreInteractions( nodeService, translator );
    }

    @Test
    public void testExecute_NodeNotFound()
    {
        when( nodeService.getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) ) ).thenThrow( NodeNotFoundException.class );

        assertThrows( ContentNotFoundException.class, () -> createInstance().execute() );

        verify( nodeService, times( 1 ) ).
            getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) );
        verifyNoMoreInteractions( nodeService );
    }

    @Test
    public void testExecute_ContentNotFound()
    {
        when( nodeService.getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) ) ).thenReturn( node );
        when( translator.fromNodeWithAnyRootPath( any( Node.class ) ) ).thenThrow( ContentNotFoundException.class );

        assertThrows( ContentNotFoundException.class, () -> createInstance().execute() );

        verify( nodeService, times( 1 ) ).getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) );
        verify( translator, times( 1 ) ).fromNodeWithAnyRootPath( any( Node.class ) );
        verifyNoMoreInteractions( nodeService, translator );
    }

    private GetContentByIdAndVersionIdCommand createInstance()
    {
        return GetContentByIdAndVersionIdCommand.create().
            contentId( contentId ).
            versionId( versionId ).
            nodeService( nodeService ).
            translator( translator ).
            eventPublisher( eventPublisher ).
            contentTypeService( contentTypeService ).
            build();
    }

}
