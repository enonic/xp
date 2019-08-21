package com.enonic.xp.admin.impl.rest.resource.issue.json;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.rest.resource.content.json.PublishRequestJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.PublishRequestScheduleJson;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.CreatePublishRequestIssueParams;
import com.enonic.xp.issue.IssueType;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestIssueSchedule;
import com.enonic.xp.security.PrincipalKey;

public class CreateIssueJson
{
    public final IssueType type;

    public final String title;

    public final String description;

    public final List<PrincipalKey> assignees;

    public final PublishRequest publishRequest;

    public final PublishRequestIssueSchedule schedule;

    @JsonCreator
    public CreateIssueJson( @JsonProperty("type") final String type, @JsonProperty("title") final String title,
                            @JsonProperty("description") final String description,
                            @JsonProperty("approvers") final List<String> approverIds,
                            @JsonProperty("publishRequest") final PublishRequestJson publishRequest,
                            @JsonProperty("schedule") final PublishRequestScheduleJson schedule )
    {
        this.type = type == null ? IssueType.STANDARD : IssueType.valueOf( type );
        this.title = title;
        this.description = description;

        this.assignees =
            approverIds != null ? approverIds.stream().map( PrincipalKey::from ).collect( Collectors.toList() ) : Collections.emptyList();

        this.publishRequest = publishRequest != null ? publishRequest.toRequest() : null;
        this.schedule = schedule != null ? schedule.toSchedule() : null;
    }
}
