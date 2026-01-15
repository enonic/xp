package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.EditableContentWorkflow;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.UpdateWorkflowResult;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class UpdateWorkflowHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void updateWorkflowState()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        when( this.contentService.updateWorkflow( isA( UpdateWorkflowParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdateWorkflow( invocationOnMock.getArgument( 0 ), content ) );

        runFunction( "/test/UpdateWorkflowHandlerTest.js", "updateWorkflowState" );
    }

    private UpdateWorkflowResult invokeUpdateWorkflow( final UpdateWorkflowParams params, final Content content )
    {
        final EditableContentWorkflow editableWorkflow = new EditableContentWorkflow( content );

        params.getEditor().edit( editableWorkflow );

        return UpdateWorkflowResult.create().content( editableWorkflow.build() ).build();
    }
}
