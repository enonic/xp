package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentServiceImplTest_patch
    extends AbstractContentServiceTest
{

    @Test
    public void patch_content_modified_time_not_changed()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final PatchContentParams patchContentParams = PatchContentParams.create().contentId( content.getId() ).patcher( edit -> {
            edit.displayName.setValue( "new display name" );
        } ).build();

        this.contentService.patch( patchContentParams );

        final Content patchedContent = this.contentService.getById( content.getId() );

        assertEquals( "new display name", patchedContent.getDisplayName() );
        assertEquals( patchedContent.getCreatedTime(), content.getCreatedTime() );
        assertEquals( patchedContent.getModifiedTime(), content.getModifiedTime() );
        assertEquals( patchedContent.getModifier(), content.getModifier() );
    }
}
