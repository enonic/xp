package com.enonic.xp.core.content;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
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

    private Content content1, content2, child1, child2;

    @Test
    public void resolve_single()
        throws Exception
    {
        nodeService.push( NodeIds.from( ROOT_UUID ), ContentConstants.BRANCH_MASTER );

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

        this.child2 = this.contentService.create( CreateContentParams.create().
            contentData( data ).
            displayName( "This is my child 2" ).
            parent( content2.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        return ResolvePublishDependenciesParams.create();
    }

}
