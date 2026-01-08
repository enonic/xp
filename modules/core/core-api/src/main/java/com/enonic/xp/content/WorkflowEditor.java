package com.enonic.xp.content;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
@NullMarked
public interface WorkflowEditor
{
    void edit( EditableWorkflow edit );
}
