package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ContentWorkflowInfoJson
{
    private WorkflowState state;

    private Map<String, WorkflowCheckState> checks;

    @JsonCreator
    public ContentWorkflowInfoJson(
        @JsonProperty("state") WorkflowState state,
        @JsonProperty("checks") Map<String, WorkflowCheckState> checks
    )
    {
        this.state = state;
        this.checks = checks;
    }

    public ContentWorkflowInfoJson( final WorkflowInfo workflowInfo )
    {
        this.state = workflowInfo.getState();
        this.checks = workflowInfo.getChecks();
    }

    public WorkflowState getState()
    {
        return state;
    }

    @SuppressWarnings("unused")
    public Map<String, WorkflowCheckState> getChecks()
    {
        return checks;
    }
}
