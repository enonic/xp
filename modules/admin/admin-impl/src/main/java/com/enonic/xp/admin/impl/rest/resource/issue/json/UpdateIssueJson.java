package com.enonic.xp.admin.impl.rest.resource.issue.json;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.rest.resource.content.json.PublishRequestJson;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class UpdateIssueJson
{
    public final IssueId issueId;

    public final String title;

    public final String description;

    public final IssueStatus issueStatus;

    public final List<PrincipalKey> approverIds;

    public final PublishRequest publishRequest;

    public final boolean isPublish;

    @JsonCreator
    public UpdateIssueJson( @JsonProperty("id") final String issueId, @JsonProperty("title") final String title,
                            @JsonProperty("description") final String description, @JsonProperty("status") final String status,
                            @JsonProperty("isPublish") final boolean isPublish, @JsonProperty("approvers") final List<String> approverIds,
                            @JsonProperty("publishRequest") final PublishRequestJson publishRequest )
    {
        this.issueId = IssueId.from( issueId );
        this.isPublish = isPublish;
        this.title = title;
        this.description = description;
        this.issueStatus = status != null ? IssueStatus.valueOf( status.trim().toUpperCase() ) : null;
        this.approverIds =
            approverIds != null ? approverIds.stream().map( str -> PrincipalKey.from( str ) ).collect( Collectors.toList() ) : null;
        this.publishRequest = publishRequest != null ? publishRequest.toRequest() : null;
    }

}

