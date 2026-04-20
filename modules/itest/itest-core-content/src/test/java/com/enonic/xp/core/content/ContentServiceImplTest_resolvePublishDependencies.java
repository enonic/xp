package com.enonic.xp.core.content;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.ResolvePublishDependenciesParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentServiceImplTest_resolvePublishDependencies
    extends AbstractContentServiceTest
{
    private Content content1, content2, child1, child2;

    @Test
    void resolve_single()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final CompareContentResults result = this.contentService.resolvePublishDependencies( ResolvePublishDependenciesParams.create()
                                                                                                 .contentIds(
                                                                                                     ContentIds.from( content.getId() ) )
                                                                                                 .excludeDescendantsOf(
                                                                                                     ContentIds.from( content.getId() ) )
                                                                                                 .build() );

        assertEquals( 1, result.contentIds().getSize() );
    }

    @Test
    void resolve_children_excluded()
    {
        initContent();

        final ResolvePublishDependenciesParams.Builder builder = ResolvePublishDependenciesParams.create()
            .contentIds( ContentIds.from( content1.getId() ) )
            .excludedContentIds( ContentIds.from( child1.getId() ) );

        final CompareContentResults result = this.contentService.resolvePublishDependencies( builder.build() );

        assertEquals( 1, result.contentIds().getSize() );
        assertFalse( result.contentIds().contains( child1.getId() ) );
    }

    @Test
    void resolve_archived_dependency()
    {
        initContent();

        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( child2.getId() ) ).build() );
        this.contentService.update( new UpdateContentParams().contentId( child2.getId() ).editor( edit -> edit.displayName += "." ) );
        this.contentService.update( new UpdateContentParams().contentId( child1.getId() ).editor( edit -> edit.displayName += "." ) );

        this.contentService.archive( ArchiveContentParams.create().contentId( child1.getId() ).build() );

        final CompareContentResults result = this.contentService.resolvePublishDependencies(
            ResolvePublishDependenciesParams.create().contentIds( ContentIds.from( child2.getId() ) ).build() );

        assertEquals( 1, result.contentIds().getSize() );
    }

    @Disabled("This test is not correct; it should not be allowed to exclude parent if new")
    @Test
    void resolve_children_in_the_middle_excluded()
    {
        initContent();

        final Content child3 = this.contentService.create( CreateContentParams.create()
                                                               .contentData( new PropertyTree() )
                                                               .displayName( "This is my child 3" )
                                                               .parent( child1.getPath() )
                                                               .type( ContentTypeName.folder() )
                                                               .build() );

        final ResolvePublishDependenciesParams pushParams = ResolvePublishDependenciesParams.create()
            .contentIds( ContentIds.from( content1.getId(), content2.getId() ) )
            .excludedContentIds( ContentIds.from( child1.getId() ) )
            .build();

        final CompareContentResults result = this.contentService.resolvePublishDependencies( pushParams );

        assertEquals( 4, result.contentIds().getSize() );

        assertFalse( result.contentIds().contains( child1.getId() ) );
        assertTrue( result.contentIds().contains( child3.getId() ) );
    }

    private void initContent()
    {
        this.content1 = this.contentService.create( CreateContentParams.create()
                                                        .contentData( new PropertyTree() )
                                                        .displayName( "This is my content" )
                                                        .parent( ContentPath.ROOT )
                                                        .type( ContentTypeName.folder() )
                                                        .build() );

        this.content2 = this.contentService.create( CreateContentParams.create()
                                                        .contentData( new PropertyTree() )
                                                        .displayName( "This is my content 2" )
                                                        .parent( ContentPath.ROOT )
                                                        .type( ContentTypeName.folder() )
                                                        .build() );

        this.child1 = this.contentService.create( CreateContentParams.create()
                                                      .contentData( new PropertyTree() )
                                                      .displayName( "This is my child 1" )
                                                      .parent( content1.getPath() )
                                                      .type( ContentTypeName.folder() )
                                                      .build() );

        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( child1.getId().toString() ) );

        this.child2 = this.contentService.create( CreateContentParams.create()
                                                      .contentData( data )
                                                      .displayName( "This is my child 2" )
                                                      .parent( content2.getPath() )
                                                      .type( ContentTypeName.folder() )
                                                      .build() );
    }

}
