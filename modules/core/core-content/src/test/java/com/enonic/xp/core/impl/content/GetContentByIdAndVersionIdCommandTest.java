package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GetContentByIdAndVersionIdCommandTest
{
    private final ContentId contentId = ContentId.from( "contentId" );

    private final ContentVersionId versionId = ContentVersionId.from( "versionId" );

    private NodeService nodeService;

    private ContentTypeService contentTypeService;

    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp()
    {
        nodeService = Mockito.mock( NodeService.class );
        contentTypeService = Mockito.mock( ContentTypeService.class );
        eventPublisher = Mockito.mock( EventPublisher.class );
    }

    @Test
    void testExecute()
    {
        when( nodeService.getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) ) ).thenReturn( ContentFixture.someContentNode() );

        final Content result = createInstance().execute();

        assertNotNull( result );

        verify( nodeService, times( 1 ) ).getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) );
        verifyNoMoreInteractions( nodeService );
    }

    @Test
    void testExecute_NodeNotFound()
    {
        when( nodeService.getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) ) ).thenThrow( NodeNotFoundException.class );

        assertThrows( ContentNotFoundException.class, () -> createInstance().execute() );

        verify( nodeService, times( 1 ) ).
            getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) );
        verifyNoMoreInteractions( nodeService );
    }

    @Test
    void testExecute_ContentNotFound()
    {
        when( nodeService.getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) ) ).thenThrow( NodeNotFoundException.class );

        assertThrows( ContentNotFoundException.class, () -> createInstance().execute() );

        verify( nodeService, times( 1 ) ).getByIdAndVersionId( any( NodeId.class ), any( NodeVersionId.class ) );
        verifyNoMoreInteractions( nodeService );
    }

    private GetContentByIdAndVersionIdCommand createInstance()
    {
        return GetContentByIdAndVersionIdCommand.create().
            contentId( contentId ).
            versionId( versionId ).
            nodeService( nodeService ).
            eventPublisher( eventPublisher ).
            contentTypeService( contentTypeService ).
            build();
    }

}
