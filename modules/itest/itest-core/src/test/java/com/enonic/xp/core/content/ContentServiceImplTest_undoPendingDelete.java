package com.enonic.xp.core.content;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UndoPendingDeleteContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ContentServiceImplTest_undoPendingDelete
    extends AbstractContentServiceTest
{
    @Test
    public void not_deleted_not_resurrected()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            name( "myContent" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        int result = resurrect( ContentIds.from( ContentIds.from( content.getId() ) ) );
        assertEquals( 0, result );
    }

    @Test
    public void single_resurrected()
        throws Exception
    {

        final Content content = this.createTestContent( "myContent" );

        int result = resurrect( ContentIds.from( ContentIds.from( content.getId() ) ) );

        assertEquals( 1, result );
    }

    @Test
    public void two_resurrected()
        throws Exception
    {
        final Content content1 = this.createTestContent( "myContent" );
        final Content content2 = this.createTestContent( "myOtherContent" );

        int result = resurrect( ContentIds.from( ContentIds.from( content1.getId(), content2.getId() ) ) );

        assertEquals( 2, result );
    }

    @Test
    public void child_resurrected()
        throws Exception
    {
        final Content parent = this.createTestContent( "myContent" );
        this.createTestContent( "myOtherContent", parent.getPath() );
        final Content child2 = this.createTestContent( "myOtherContent2", parent.getPath() );

        this.nodeService.setNodeState( SetNodeStateParams.create().
            nodeId( NodeId.from( child2.getId() ) ).
            recursive( true ).
            nodeState( NodeState.DEFAULT ).
            build() );

        int result = resurrect( ContentIds.from( parent.getId() ) );
        assertEquals( 2, result );
    }

    @Test
    public void deleted_parents_resurrected()
        throws Exception
    {
        final Content node1 = this.createTestContent( "node1", ContentPath.ROOT );
        this.createTestContent( "node2", ContentPath.ROOT );
        final Content node1_1 = this.createTestContent( "node1_1", node1.getPath() );
        this.createTestContent( "node1_2", node1.getPath() );
        final Content node1_1_1 = this.createTestContent( "node1_1_1", node1_1.getPath() );

        final int result = resurrect( ContentIds.from( node1_1_1.getId() ) );

        assertEquals( 3, result );
    }

    private int resurrect( final ContentIds contentIds )
    {
        return this.contentService.undoPendingDelete( UndoPendingDeleteContentParams.create().
            contentIds( contentIds ).
            target( CTX_OTHER.getBranch() ).
            build() );
    }

    private Content createTestContent( final String name )
    {
        return this.createTestContent( name, null );
    }

    private Content createTestContent( final String name, final ContentPath parentPath )
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            name( name ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            parent( parentPath == null ? ContentPath.ROOT : parentPath ).
            build();

        final Content content = this.contentService.create( createContentParams );

        this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( WS_OTHER ).
            includeDependencies( false ).
            build() );

        this.nodeService.setNodeState( SetNodeStateParams.create().
            nodeId( NodeId.from( content.getId() ) ).
            recursive( true ).
            nodeState( NodeState.PENDING_DELETE ).
            build() );

        return content;
    }
}
