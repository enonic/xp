package com.enonic.xp.admin.impl.rest.resource.issue.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.security.PrincipalKey;

public class CreateIssueJson
{

    private final CreateIssueParams createIssueParams;

    @JsonCreator
    CreateIssueJson( @JsonProperty("title") final String title, @JsonProperty("description") final String description,
                     @JsonProperty("approvers") final List<String> approverIds, @JsonProperty("items") final List<String> contentIds )
    {

        final CreateIssueParams.Builder paramsBuilder = CreateIssueParams.create();
        paramsBuilder.title( title );
        paramsBuilder.description( description );

        if ( approverIds != null )
        {
            approverIds.forEach( approverId -> paramsBuilder.addApproverId( PrincipalKey.from( approverId ) ) );
        }
        if ( contentIds != null )
        {
            contentIds.forEach( contentId -> paramsBuilder.addItemId( ContentId.from( contentId ) ) );
        }
        this.createIssueParams = paramsBuilder.build();
    }

    @JsonIgnore
    public CreateIssueParams getCreateIssueParams()
    {
        return createIssueParams;
    }
}
