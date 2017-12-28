package com.enonic.xp.admin.impl.rest.resource.issue.json;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.rest.resource.content.json.PublishRequestJson;
import com.enonic.xp.issue.Comment;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.security.PrincipalKey;

public class CreateIssueJson
{
    public final String title;

    public final String description;

    public final List<PrincipalKey> assignees;

    public final PublishRequest publishRequest;

    public final List<Comment> comments;

    @JsonCreator
    public CreateIssueJson( @JsonProperty("title") final String title, @JsonProperty("description") final String description,
                            @JsonProperty("approvers") final List<String> approverIds,
                            @JsonProperty("comments") final List<CommentJson> commentJsons,
                            @JsonProperty("publishRequest") final PublishRequestJson publishRequest )
    {

        this.title = title;
        this.description = description;

        this.assignees =
            approverIds != null ? approverIds.stream().map( PrincipalKey::from ).collect( Collectors.toList() ) : Collections.emptyList();

        this.comments = commentJsons != null
            ? commentJsons.stream().map( CommentJson::toComment ).collect( Collectors.toList() )
            : Collections.emptyList();

        this.publishRequest = publishRequest != null ? publishRequest.toRequest() : null;
    }

}
