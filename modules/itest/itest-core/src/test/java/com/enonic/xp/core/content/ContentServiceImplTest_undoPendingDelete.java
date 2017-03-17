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
    public void test_default_state_content_not_updated()
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

        int result = this.contentService.undoPendingDelete(
            new UndoPendingDeleteContentParams( ContentIds.from( content.getId() ), CTX_DEFAULT.getBranch() ) );
        assertEquals( 0, result );
    }

    @Test
    public void test_single_content_updated()
        throws Exception
    {

        final Content content = this.createTestContent( "myContent" );

        int result = this.contentService.undoPendingDelete(
            new UndoPendingDeleteContentParams( ContentIds.from( content.getId() ), CTX_OTHER.getBranch() ) );
        assertEquals( 1, result );
    }

    @Test
    public void test_two_contents_updated()
        throws Exception
    {
        final Content content1 = this.createTestContent( "myContent" );
        final Content content2 = this.createTestContent( "myOtherContent" );

        int result = this.contentService.undoPendingDelete(
            new UndoPendingDeleteContentParams( ContentIds.from( content1.getId(), content2.getId() ), CTX_OTHER.getBranch() ) );
        assertEquals( 2, result );
    }

    @Test
    public void test_child_content_also_updated()
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

        int result = this.contentService.undoPendingDelete(
            new UndoPendingDeleteContentParams( ContentIds.from( parent.getId() ), CTX_OTHER.getBranch() ) );
        assertEquals( 2, result );
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
