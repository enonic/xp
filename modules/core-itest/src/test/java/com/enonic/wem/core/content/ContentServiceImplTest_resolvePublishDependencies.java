package com.enonic.wem.core.content;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
import com.enonic.xp.content.ResolvePublishDependenciesResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class ContentServiceImplTest_resolvePublishDependencies
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void resolve_one_content()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        ResolvePublishDependenciesResult result = contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            target( CTX_OTHER.getBranch() ).
            contentIds( ContentIds.from( content.getId() ) ).
            build() );

        assertFalse( result.getResolvedContent().contains( content ) );
    }
/*
    @Test
    public void push_deleted()
        throws Exception
    {
        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final PushContentParams pushParams = PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            allowPublishOutsideSelection( false ).
            build();

        this.contentService.push( pushParams );

        contentService.delete( DeleteContentParams.create().
            contentPath( content.getPath() ).
            build() );

        final PushContentsResult pushWithDeleted = this.contentService.push( pushParams );

        assertEquals( 1, pushWithDeleted.getDeleted().getSize() );
    }*/

    @Test
    public void resolve_against_child_with_reference()
        throws Exception
    {
        final ResolvePublishDependenciesResult result = doResolveWithDependencies( false, false, false, true );

        assertEquals( 3, result.getDependantsIdsResolvedWithChildrenIncluded().getSize() );
        assertEquals( 0, result.getChildrenContentsIds().getSize() );
    }

    @Test
    public void resolve_against_content_with_child_no_refs()
        throws Exception
    {
        final ResolvePublishDependenciesResult result = doResolveWithDependencies( true, false, false, false );

        assertEquals( 1, result.getChildrenContentsIds().getSize() );
        assertEquals( 0, result.getDependantsIdsResolvedWithChildrenIncluded().getSize() );
    }

    @Test
    public void resolve_against_content_with_child_with_refs()
        throws Exception
    {
        final ResolvePublishDependenciesResult result = doResolveWithDependencies( false, true, false, false );

        assertEquals( 1, result.getChildrenContentsIds().getSize() );
        assertEquals( 2, result.getDependantsIdsResolvedWithChildrenIncluded().getSize() );
        assertEquals( 0, result.getDependantsIdsResolvedWithoutChildrenIncluded().getSize() );
    }

    private ResolvePublishDependenciesResult doResolveWithDependencies( boolean isCont1, boolean isCont2, boolean isChild1,
                                                                        boolean isChild2 )
    {
        final Content content1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final Content content2 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content 2" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final Content child1 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my child 1" ).
            parent( content1.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( child1.getId().toString() ) );

        final Content child2 = this.contentService.create( CreateContentParams.create().
            contentData( data ).
            displayName( "This is my child 2" ).
            parent( content2.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        ContentIds ids;
        if ( isCont1 )
        {
            ids = ContentIds.from( content1.getId() );
        }
        else if ( isCont2 )
        {
            ids = ContentIds.from( content2.getId() );
        }
        else if ( isChild1 )
        {
            ids = ContentIds.from( child1.getId() );
        }
        else
        {
            ids = ContentIds.from( child2.getId() );
        }

        return contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create().
            target( CTX_OTHER.getBranch() ).
            contentIds( ids ).
            build() );

    }


}
