package com.enonic.xp.core.content;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentServiceImplTest_resolvePublishDependencies
    extends AbstractContentServiceTest
{
    private static final NodeId ROOT_UUID = NodeId.from( "000-000-000-000" );

    private Content content1;

    private Content content2;

    private Content child1;

    private Content s1;

    private Content a2;

    private Content a21;

    private Content a211;

    private Content b21;

    @Test
    public void resolve_single()
        throws Exception
    {
        nodeService.push( NodeIds.from( ROOT_UUID ), WS_OTHER );

        refresh();

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        refresh();

        final CompareContentResults result = this.contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            excludeChildrenIds( ContentIds.from( content.getId() ) ).
            target( WS_OTHER ).
            build() );

        assertEquals( 1, result.contentIds().getSize() );
    }

    @Test
    public void resolve_children_excluded()
        throws Exception
    {
        final ResolvePublishDependenciesParams.Builder builder = getPushParamsWithDependenciesBuilder().
            contentIds( ContentIds.from( content1.getId() ) ).
            excludedContentIds( ContentIds.from( child1.getId() ) );

        refresh();

        final CompareContentResults result = this.contentService.resolvePublishDependencies( builder.build() );

        assertEquals( 1, result.contentIds().getSize() );
        assertFalse( result.contentIds().contains( child1.getId() ) );
    }

    @Test
    public void exclude_with_batch_size()
    {
        final Content parent = createContent( ContentPath.ROOT, "parent" );

        createChildContents( parent.getPath(), 601, WorkflowState.READY, false );
        createChildContents( parent.getPath(), 602, WorkflowState.IN_PROGRESS, true );
        createChildContents( parent.getPath(), 603, WorkflowState.IN_PROGRESS, false );
        createChildContents( parent.getPath(), 604, WorkflowState.PENDING_APPROVAL, true );

        refresh();

        CompareContentResults result = this.contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            target( WS_OTHER ).
            contentIds( ContentIds.from( parent.getId() ) ).
            excludeInvalid( true ).
            build() );

        assertEquals( 1207, result.size() ); //602 + 604 + root

        result = this.contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            target( WS_OTHER ).
            contentIds( ContentIds.from( parent.getId() ) ).
            setExcludeWorkflowStates( Set.of( WorkflowState.IN_PROGRESS ) ).
            build() );
        assertEquals( 1206, result.size() );// 601 + 604 + root

        result = this.contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            target( WS_OTHER ).
            contentIds( ContentIds.from( parent.getId() ) ).
            setExcludeWorkflowStates( Set.of( WorkflowState.READY, WorkflowState.PENDING_APPROVAL ) ).
            build() );
        assertEquals( 1206, result.size() );// 602 + 603 + root

        result = this.contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            target( WS_OTHER ).
            contentIds( ContentIds.from( parent.getId() ) ).
            setExcludeWorkflowStates( Set.of( WorkflowState.IN_PROGRESS ) ).
            excludeInvalid( true ).
            build() );
        assertEquals( 605, result.size() ); // 604 + root
    }

    @Test
    public void exclude_invalid_direct_reference()
    {
        createS1S2Tree();

        refresh();

        // make content invalid
        contentService.update( new UpdateContentParams().
            contentId( b21.getId() ).
            editor( edit -> edit.data = new PropertyTree() ) );

        refresh();

        final CompareContentResults result = this.contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            target( WS_OTHER ).
            excludeInvalid( true ).
            contentIds( ContentIds.from( a21.getId() ) ).
            excludeChildrenIds( ContentIds.from( a21.getId() ) ).
            build() );

        assertEquals( 3, result.size() );
        assertTrue( result.contentIds().contains( a21.getId() ) );
        assertTrue( result.contentIds().contains( a2.getId() ) );
        assertTrue( result.contentIds().contains( s1.getId() ) );
    }

    @Test
    public void exclude_in_progress_direct_reference()
    {
        createS1S2Tree();

        refresh();

        contentService.update( new UpdateContentParams().
            contentId( b21.getId() ).
            editor( edit -> edit.workflowInfo = WorkflowInfo.create().state( WorkflowState.IN_PROGRESS ).build() ) );

        refresh();

        final CompareContentResults result = this.contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            target( WS_OTHER ).
            setExcludeWorkflowStates( Set.of( WorkflowState.IN_PROGRESS ) ).
            contentIds( ContentIds.from( a21.getId() ) ).
            excludeChildrenIds( ContentIds.from( a21.getId() ) ).
            build() );

        assertEquals( 3, result.size() );
        assertTrue( result.contentIds().contains( a21.getId() ) );
        assertTrue( result.contentIds().contains( a2.getId() ) );
        assertTrue( result.contentIds().contains( s1.getId() ) );
    }

    @Test
    public void exclude_invalid_parent_reference()
    {
        createS1S2Tree();

        refresh();

        // make content invalid
        contentService.update( new UpdateContentParams().
            contentId( b21.getId() ).
            editor( edit -> edit.data = new PropertyTree() ) );

        refresh();

        final CompareContentResults result = this.contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            target( WS_OTHER ).
            excludeInvalid( true ).
            contentIds( ContentIds.from( a211.getId() ) ).
            build() );

        assertEquals( 4, result.size() );
        assertTrue( result.contentIds().contains( a211.getId() ) );
        assertTrue( result.contentIds().contains( a21.getId() ) );
        assertTrue( result.contentIds().contains( a2.getId() ) );
        assertTrue( result.contentIds().contains( s1.getId() ) );
    }

    @Test
    public void exclude_in_progress_parent_reference()
    {
        createS1S2Tree();

        refresh();

        contentService.update( new UpdateContentParams().
            contentId( b21.getId() ).
            editor( edit -> edit.workflowInfo = WorkflowInfo.create().state( WorkflowState.IN_PROGRESS ).build() ) );

        refresh();

        final CompareContentResults result = this.contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            target( WS_OTHER ).
            setExcludeWorkflowStates( Set.of( WorkflowState.IN_PROGRESS ) ).
            contentIds( ContentIds.from( a211.getId() ) ).
            build() );

        assertEquals( 4, result.size() );
        assertTrue( result.contentIds().contains( a211.getId() ) );
        assertTrue( result.contentIds().contains( a21.getId() ) );
        assertTrue( result.contentIds().contains( a2.getId() ) );
        assertTrue( result.contentIds().contains( s1.getId() ) );
    }


    @Disabled("This test is not correct; it should not be allowed to exclude parent if new")
    @Test
    public void resolve_children_in_the_middle_excluded()
        throws Exception
    {
        final ResolvePublishDependenciesParams.Builder builder = getPushParamsWithDependenciesBuilder();

        final Content child3 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my child 3" ).
            parent( child1.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        refresh();

        final ResolvePublishDependenciesParams pushParams = builder.
            contentIds( ContentIds.from( content1.getId(), content2.getId() ) ).
            excludedContentIds( ContentIds.from( child1.getId() ) ).
            build();

        final CompareContentResults result = this.contentService.resolvePublishDependencies( pushParams );

        assertEquals( 4, result.contentIds().getSize() );

        assertFalse( result.contentIds().contains( child1.getId() ) );
        assertTrue( result.contentIds().contains( child3.getId() ) );
    }

    private ResolvePublishDependenciesParams.Builder getPushParamsWithDependenciesBuilder()
    {
        this.content1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        this.content2 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content 2" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        this.child1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my child 1" ).
            parent( content1.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( child1.getId().toString() ) );

        this.contentService.create( CreateContentParams.create().
            contentData( data ).
            displayName( "This is my child 2" ).
            parent( content2.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        return ResolvePublishDependenciesParams.create().
            target( WS_OTHER );
    }

    private void createChildContents( final ContentPath parent, final int size, final WorkflowState state, final boolean isValid )
    {
        for ( int i = 0; i < size; i++ )
        {
            contentService.create( CreateContentParams.create().
                displayName( "displayName-" + UUID.randomUUID().toString() ).
                parent( parent ).
                contentData( new PropertyTree() ).
                workflowInfo( WorkflowInfo.create().state( state ).build() ).
                type( isValid ? ContentTypeName.folder() : ContentTypeName.shortcut() ).
                build() );
        }
    }

    /*
     * s1
     ** a1
     ** a2
     *** a2_1 -> b2_1
     **** a2_1_1
     * s2
     ** b1
     ** b2
     *** b2_1
     */
    private void createS1S2Tree()
    {
        s1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "s1" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "a1" ).
            parent( s1.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        a2 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "a2" ).
            parent( s1.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        final Content s2 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "s2" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "b1" ).
            parent( s2.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        final Content b2 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "b2" ).
            parent( s2.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        PropertyTree data = new PropertyTree();
        data.addReference( "target", Reference.from( "123" ) );

        b21 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "b2_1" ).
            parent( b2.getPath() ).
            contentData( data ).
            type( ContentTypeName.shortcut() ).
            build() );

        a21 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "a2_1" ).
            parent( a2.getPath() ).
            contentData( createDataWithReferences( Reference.from( b21.getId().toString() ) ) ).
            type( ContentTypeName.folder() ).
            build() );

        a211 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "a2_1_1" ).
            parent( a21.getPath() ).
            type( ContentTypeName.folder() ).
            build() );
    }

    private PropertyTree createDataWithReferences( final Reference... references )
    {
        PropertyTree data = new PropertyTree();

        for ( final Reference reference : references )
        {
            data.setReference( reference.getNodeId().toString(), reference );
        }

        return data;
    }
}
