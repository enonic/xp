package com.enonic.xp.admin.impl.rest.resource.content.page.fragment;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.json.content.ContentWorkflowInfoJson;
import com.enonic.xp.admin.impl.json.content.page.region.ComponentJson;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.region.Component;

public class CreateFragmentJson
{
    private final Component component;

    private final PropertyTree config;

    private final ContentId parent;

    private final WorkflowInfo workflowInfo;

    @JsonCreator
    public CreateFragmentJson( @JsonProperty("contentId") final String contentId,
                               @JsonProperty("config") final List<PropertyArrayJson> configJson,
                               @JsonProperty("component") final ComponentJson componentJson,
                               @JsonProperty("workflow") final ContentWorkflowInfoJson workflowInfoJson )
    {
        this.component = componentJson != null ? componentJson.getComponent() : null;
        this.config = configJson != null ? PropertyTreeJson.fromJson( configJson ) : null;
        this.parent = ContentId.from( contentId );
        this.workflowInfo = workflowInfoJson == null ? WorkflowInfo.ready() : workflowInfoJson.getWorkflowInfo();
    }

    @JsonIgnore
    public Component getComponent()
    {
        return component;
    }

    @JsonIgnore
    public PropertyTree getConfig()
    {
        return config;
    }

    @JsonIgnore
    public ContentId getParent()
    {
        return parent;
    }

    @JsonIgnore
    public WorkflowInfo getWorkflowInfo()
    {
        return workflowInfo;
    }
}
