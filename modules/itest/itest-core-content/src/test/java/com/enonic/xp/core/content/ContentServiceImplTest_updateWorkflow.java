package com.enonic.xp.core.content;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.UpdateWorkflowResult;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContentServiceImplTest_updateWorkflow
    extends AbstractContentServiceTest
{
    @Test
    void update_workflow_info()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .workflowInfo( WorkflowInfo.inProgress() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateWorkflowParams params = UpdateWorkflowParams.create()
            .contentId( content.getId() )
            .editor( edit -> edit.workflow = WorkflowInfo.create().state( WorkflowState.PENDING_APPROVAL ).build() )
            .build();

        final UpdateWorkflowResult result = this.contentService.updateWorkflow( params );

        assertNotNull( result );
        assertNotNull( result.getContent() );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getWorkflowInfo() );
        assertNotNull( storedContent.getWorkflowInfo().getState() );
        assertEquals( WorkflowState.PENDING_APPROVAL, storedContent.getWorkflowInfo().getState() );
    }

    @Test
    void update_workflow_does_not_reset_name_inherit()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .name( "content" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .workflowInfo( WorkflowInfo.inProgress() )
                                                                .build() );

        this.contentService.patch( PatchContentParams.create()
                                       .contentId( content.getId() )
                                       .patcher( edit -> edit.inherit.setValue(
                                           EnumSet.of( ContentInheritType.CONTENT, ContentInheritType.NAME, ContentInheritType.SORT,
                                                       ContentInheritType.PARENT ) ) )
                                       .build() );

        this.contentService.updateWorkflow( UpdateWorkflowParams.create()
                                                .contentId( content.getId() )
                                                .editor( edit -> edit.workflow =
                                                    WorkflowInfo.create().state( WorkflowState.PENDING_APPROVAL ).build() )
                                                .build() );

        final Content updatedContent = this.contentService.getById( content.getId() );
        assertThat( updatedContent.getInherit() ).doesNotContain( ContentInheritType.CONTENT )
            .contains( ContentInheritType.NAME, ContentInheritType.SORT, ContentInheritType.PARENT );
    }
}
