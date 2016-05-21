package com.enonic.xp.core.content;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class ContentServiceImplTest_push
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void push_one_content()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        refresh();

        final PushContentsResult push = this.contentService.push( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            includeDependencies( false ).
            build() );

        assertEquals( 0, push.getDeletedContents().getSize() );
        assertEquals( 0, push.getFailedContents().getSize() );
        assertEquals( 1, push.getPushedContents().getSize() );
    }

    @Ignore
    @Test
    public void push_one_content_not_valid()
        throws Exception
    {

        ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapplication:test" ).
            addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() ).
            build();

        Mockito.when( this.contentTypeService.getByName( GetContentTypeParams.from( contentType.getName() ) ) ).
            thenReturn( contentType );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( contentType.getName() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final PushContentsResult push = this.contentService.push( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            includeDependencies( false ).
            build() );

        assertEquals( 1, push.getPushedContents().getSize() );
    }


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
            build();

        final PushContentsResult push = this.contentService.push( pushParams );
        assertEquals( 1, push.getPushedContents().getSize() );

        contentService.delete( DeleteContentParams.create().
            contentPath( content.getPath() ).
            build() );

        final PushContentsResult pushWithDeleted = this.contentService.push( pushParams );
        assertEquals( 1, pushWithDeleted.getDeletedContents().getSize() );
    }

    @Test
    public void push_dependencies()
        throws Exception
    {
        final PushContentsResult result = doPushWithDependencies();

        assertEquals( 4, result.getPushedContents().getSize() );
    }

    private Content content1, content2, child1, child2;


    @Test
    public void push_exclude_empty()
        throws Exception
    {

        final PushContentParams.Builder builder = getPushParamsWithDependenciesBuilder();

        final PushContentParams pushParams = builder.
            contentIds( ContentIds.from( content1.getId() ) ).
            excludedContentIds( ContentIds.from( content1.getId() ) ).
            build();

        refresh();

        final PushContentsResult result = this.contentService.push( pushParams );

        assertEquals( 0, result.getPushedContents().getSize() );
    }

    @Test
    public void push_exclude_without_children()
        throws Exception
    {

        final PushContentParams.Builder builder = getPushParamsWithDependenciesBuilder();

        final PushContentParams pushParams = builder.
            contentIds( ContentIds.from( content1.getId() ) ).
            excludedContentIds( ContentIds.from( child1.getId() ) ).
            includeChildren( false ).
            build();

        refresh();

        final PushContentsResult result = this.contentService.push( pushParams );

        assertEquals( 1, result.getPushedContents().getSize() );
    }

    @Test
    public void push_exclude_with_children()
        throws Exception
    {

        final PushContentParams.Builder builder = getPushParamsWithDependenciesBuilder();

        final Content child3 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my child 3" ).
            parent( child1.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        refresh();

        final PushContentParams pushParams = builder.
            contentIds( ContentIds.from( content1.getId(), content2.getId() ) ).
            excludedContentIds( ContentIds.from( child1.getId() ) ).
            build();

        final PushContentsResult result = this.contentService.push( pushParams );

        assertEquals( 3, result.getPushedContents().getSize() );
        assertFalse( result.getPushedContents().contains( child1.getId() ) );
        assertFalse( result.getPushedContents().contains( child3.getId() ) );
    }

    @Test
    public void push_exclude_without_dependencies()
        throws Exception
    {

        final PushContentParams.Builder builder = getPushParamsWithDependenciesBuilder();

        refresh();

        final PushContentParams pushParams = builder.
            contentIds( ContentIds.from( content1.getId(), content2.getId() ) ).
            includeDependencies( false ).
            build();

        final PushContentsResult result = this.contentService.push( pushParams );

        assertEquals( 2, result.getPushedContents().getSize() );
        assertFalse( result.getPushedContents().contains( child1.getId() ) );
        assertFalse( result.getPushedContents().contains( child2.getId() ) );
    }


    private PushContentsResult doPushWithDependencies()
    {
        final PushContentParams pushParams = getPushParamsWithDependenciesBuilder().
            contentIds( ContentIds.from( child2.getId() ) ).
            build();

        refresh();
        return this.contentService.push( pushParams );
    }


    private PushContentParams.Builder getPushParamsWithDependenciesBuilder()
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

        return PushContentParams.create().
            target( CTX_OTHER.getBranch() );
    }
}
