package com.enonic.xp.core.content;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentServiceImplTest_deleteAndFetch
    extends AbstractContentServiceTest
{

    @Test
    public void create_delete_content()
        throws Exception
    {
        //Creates a content
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        //Deletes the content
        final DeleteContentParams deleteContentParams = DeleteContentParams.create().contentPath( content.getPath() ).build();

        final Contents deletedContents = this.contentService.delete( deleteContentParams );
        assertNotNull( deletedContents );
        assertEquals( content.getId(), deletedContents.first().getId() );

        //Checks that the content is deleted
        final ContentIds contentIds = ContentIds.from( content.getId() );
        final GetContentByIdsParams getContentByIdsParams = new GetContentByIdsParams( contentIds );

        final Contents foundContents = this.contentService.getByIds( getContentByIdsParams );
        assertEquals( 0, foundContents.getSize() );
    }

    @Test
    public void create_delete_content_with_children()
        throws Exception
    {
        //Creates a content with children
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Root Content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final CreateContentParams createChild1ContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Child1 Content" ).
            parent( content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content child1Content = this.contentService.create( createChild1ContentParams );

        final CreateContentParams createChild2ContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Child2 Content" ).
            parent( content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content child2Content = this.contentService.create( createChild2ContentParams );

        final CreateContentParams createSubChildContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "SubChild Content" ).
            parent( child1Content.getPath() ).
            type( ContentTypeName.folder() ).
            build();

        final Content subChildContent = this.contentService.create( createSubChildContentParams );

        refresh();

        //Deletes the content
        final Contents deletedContents =
            this.contentService.delete( DeleteContentParams.create().contentPath( content.getPath() ).build() );

        assertNotNull( deletedContents );
        assertTrue( deletedContents.stream().map( curContent -> curContent.getId().toString() ).collect( Collectors.toList() ).contains(
            content.getId().toString() ) );
        //Checks that the content and the children are deleted
        final GetContentByIdsParams getContentByIdsParams = new GetContentByIdsParams(
            ContentIds.from( content.getId(), child1Content.getId(), child2Content.getId(), subChildContent.getId() ) );

        final Contents foundContents = this.contentService.getByIds( getContentByIdsParams );

        assertEquals( 0, foundContents.getSize() );
    }

    @Test
    public void create_content_with_same_paths_in_two_repos_then_delete()
        throws Exception
    {
        final CreateContentParams params = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( params );

        final Content contentOther = CTX_OTHER.callWith( () -> this.contentService.create( params ) );

        //Deletes the content
        final Contents deletedContents =
            this.contentService.delete( DeleteContentParams.create().contentPath( content.getPath() ).build() );
        assertNotNull( deletedContents );
        assertEquals( 1, deletedContents.getSize() );

        final Contents deletedOther = CTX_OTHER.callWith(
            () -> this.contentService.delete( DeleteContentParams.create().contentPath( contentOther.getPath() ).build() ) );

        assertNotNull( deletedOther );
        assertEquals( 1, deletedOther.getSize() );
    }


}
