package com.enonic.xp.content;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
@NullMarked
public final class EditableContentWorkflow
{
    public final Content source;

    public WorkflowInfo workflow;

    public EditableContentWorkflow( final Content source )
    {
        this.source = source;
        this.workflow = source.getWorkflowInfo();
    }

    public Content build()
    {
        return Content.create( source ).workflowInfo( workflow ).build();
    }
}
