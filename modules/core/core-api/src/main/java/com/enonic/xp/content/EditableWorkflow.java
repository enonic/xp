package com.enonic.xp.content;

import java.util.HashMap;
import java.util.Map;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class EditableWorkflow
{
    public final WorkflowInfo source;

    public WorkflowState state;

    public Map<String, WorkflowCheckState> checks;

    public EditableWorkflow( final WorkflowInfo source )
    {
        this.source = source;
        this.state = source != null ? source.getState() : null;
        this.checks = source != null && source.getChecks() != null
            ? new HashMap<>( source.getChecks() )
            : new HashMap<>();
    }

    public WorkflowInfo build()
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

        return builder.build();
    }
}
