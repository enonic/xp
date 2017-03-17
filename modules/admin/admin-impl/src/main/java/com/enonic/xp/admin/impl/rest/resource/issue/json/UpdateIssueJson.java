package com.enonic.xp.admin.impl.rest.resource.issue.json;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class UpdateIssueJson
{
    private final UpdateIssueParams updateIssueParams;

    @JsonCreator
    UpdateIssueJson( @JsonProperty("id") final String issueId, @JsonProperty("title") final String title,
                     @JsonProperty("description") final String description, @JsonProperty("status") final String status,
                     @JsonProperty("approvers") final List<String> approverIds, @JsonProperty("items") final List<String> contentIds )
    {

        updateIssueParams = new UpdateIssueParams().
            id( IssueId.from( issueId ) ).
            editor( editMe -> {
                if ( title != null )
                {
                    editMe.title = title;
                }
                if ( description != null )
                {
                    editMe.description = description;
                }
                if ( status != null )
                {
                    editMe.issueStatus = IssueStatus.valueOf( status );
                }
                if ( approverIds != null )
                {
                    editMe.approverIds =
                        PrincipalKeys.from( approverIds.stream().map( str -> PrincipalKey.from( str ) ).collect( Collectors.toList() ) );
                }
                if ( contentIds != null )
                {
                    editMe.itemIds =
                        ContentIds.from( contentIds.stream().map( str -> ContentId.from( str ) ).collect( Collectors.toList() ) );
                }
            } );
    }

    @JsonIgnore
    public UpdateIssueParams getUpdateIssueParams()
    {
        return updateIssueParams;
    }

}

