package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentServiceImplTest_versions
    extends AbstractContentServiceTest
{

    @Test
    void get_versions()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).editor( edit -> edit.displayName = "new display name" );

        this.contentService.update( updateContentParams );

        final FindContentVersionsResult result =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( content.getId() ).build() );

        assertEquals( 2, result.getContentVersions().getSize() );
        assertEquals( 2, result.getTotalHits() );
    }

    @Test
    void getVersions_no_role()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        assertThrows( ForbiddenAccessException.class, () -> ContextBuilder.create()
            .branch( ContentConstants.BRANCH_DRAFT )
            .repositoryId( testprojectName.getRepoId() )
            .build()
            .runWith( () -> this.contentService.getVersions( FindContentVersionsParams.create().contentId( content.getId() ).build() ) ) );
    }

    @Test
    void get_archived_versions()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        this.contentService.restore( RestoreContentParams.create().contentId( content.getId() ).build() );

        final FindContentVersionsResult result =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( content.getId() ).build() );

        assertEquals( 3, result.getContentVersions().getSize() );
        assertEquals( 3, result.getTotalHits() );

        assertThat( result.getContentVersions() ).elements( 0, 1 )
            .extracting( ContentVersion::getActions )
            .map( cs -> cs.stream().map( ContentVersion.Action::operation ).findFirst().orElseThrow() )
            .containsExactly( "content.restore", "content.archive" );
    }
}

