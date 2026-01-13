package com.enonic.xp.content;

import java.util.Map;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class EditableWorkflow
{
    public final WorkflowInfo source;

    public WorkflowState state;

    public EditableWorkflow( final WorkflowInfo source )
    {
        this.source = source;
        this.state = source != null ? source.getState() : null;
    }

    public WorkflowInfo build()
    {
        final WorkflowInfo.Builder builder = WorkflowInfo.create();
        if ( state != null )
        {
            builder.state( state );
        }

        return builder.build();
    }
}
