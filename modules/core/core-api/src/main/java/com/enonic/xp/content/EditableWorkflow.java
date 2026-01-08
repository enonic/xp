package com.enonic.xp.content;

import java.util.HashMap;
import java.util.Map;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class EditableWorkflow
{
    public final Content source;

    public WorkflowState state;

    public Map<String, WorkflowCheckState> checks;

    public EditableWorkflow( final Content source )
    {
        this.source = source;
        final WorkflowInfo workflowInfo = source.getWorkflowInfo();
        this.state = workflowInfo != null ? workflowInfo.getState() : null;
        this.checks = workflowInfo != null && workflowInfo.getChecks() != null
            ? new HashMap<>( workflowInfo.getChecks() )
            : new HashMap<>();
    }

    public Content build()
    {
        final WorkflowInfo.Builder builder = WorkflowInfo.create();
        if ( state != null )
        {
            builder.state( state );
        }
        if ( checks != null )
        {
            builder.checks( checks );
        }

        return Content.create( this.source ).workflowInfo( builder.build() ).build();
    }
}
