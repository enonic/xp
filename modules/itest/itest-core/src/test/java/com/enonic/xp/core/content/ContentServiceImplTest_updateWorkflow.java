package com.enonic.xp.core.content;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.UpdateWorkflowResult;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

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
            .editor( edit -> {
                edit.state = WorkflowState.PENDING_APPROVAL;
                edit.checks.put( "Legal review", WorkflowCheckState.PENDING );
                edit.checks.put( "Manager approval", WorkflowCheckState.APPROVED );
            } )
            .build();

        final UpdateWorkflowResult result = this.contentService.updateWorkflow( params );

        assertNotNull( result );
        assertNotNull( result.getContent() );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getWorkflowInfo() );
        assertNotNull( storedContent.getWorkflowInfo().getState() );
        assertNotNull( storedContent.getWorkflowInfo().getChecks() );
        assertEquals( WorkflowState.PENDING_APPROVAL, storedContent.getWorkflowInfo().getState() );
        assertEquals( 2, storedContent.getWorkflowInfo().getChecks().size() );
        assertEquals( WorkflowCheckState.PENDING, storedContent.getWorkflowInfo().getChecks().get( "Legal review" ) );
        assertEquals( WorkflowCheckState.APPROVED, storedContent.getWorkflowInfo().getChecks().get( "Manager approval" ) );
    }

    @Test
    void update_workflow_state_only()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "Content with workflow" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .workflowInfo( WorkflowInfo.create()
                .state( WorkflowState.IN_PROGRESS )
                .checks( Map.of( "Review", WorkflowCheckState.PENDING ) )
                .build() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateWorkflowParams params = UpdateWorkflowParams.create()
            .contentId( content.getId() )
            .editor( edit -> {
                edit.state = WorkflowState.READY;
            } )
            .build();

        final UpdateWorkflowResult result = this.contentService.updateWorkflow( params );

        assertNotNull( result );
        final Content storedContent = this.contentService.getById( content.getId() );
        assertEquals( WorkflowState.READY, storedContent.getWorkflowInfo().getState() );
        // Checks should be preserved
        assertEquals( WorkflowCheckState.PENDING, storedContent.getWorkflowInfo().getChecks().get( "Review" ) );
    }

    @Test
    void update_workflow_checks_only()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "Content with workflow" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .workflowInfo( WorkflowInfo.create()
                .state( WorkflowState.IN_PROGRESS )
                .checks( Map.of( "Review", WorkflowCheckState.PENDING ) )
                .build() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateWorkflowParams params = UpdateWorkflowParams.create()
            .contentId( content.getId() )
            .editor( edit -> {
                edit.checks.put( "Review", WorkflowCheckState.APPROVED );
                edit.checks.put( "Final check", WorkflowCheckState.PENDING );
            } )
            .build();

        final UpdateWorkflowResult result = this.contentService.updateWorkflow( params );

        assertNotNull( result );
        final Content storedContent = this.contentService.getById( content.getId() );
        // State should be preserved
        assertEquals( WorkflowState.IN_PROGRESS, storedContent.getWorkflowInfo().getState() );
        assertEquals( 2, storedContent.getWorkflowInfo().getChecks().size() );
        assertEquals( WorkflowCheckState.APPROVED, storedContent.getWorkflowInfo().getChecks().get( "Review" ) );
        assertEquals( WorkflowCheckState.PENDING, storedContent.getWorkflowInfo().getChecks().get( "Final check" ) );
    }
}
