package com.enonic.xp.admin.impl.rest.resource.issue.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.rest.resource.content.json.PublishRequestJson;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.security.PrincipalKey;

public class CreateIssueJson
{

    private final CreateIssueParams createIssueParams;

    @JsonCreator
    public CreateIssueJson( @JsonProperty("title") final String title, @JsonProperty("description") final String description,
                     @JsonProperty("approvers") final List<String> approverIds, @JsonProperty("publishRequest") final PublishRequestJson publishRequest )
    {

        final CreateIssueParams.Builder paramsBuilder = CreateIssueParams.create();
        paramsBuilder.title( title );
        paramsBuilder.description( description );

        if ( approverIds != null )
        {
            approverIds.forEach( approverId -> paramsBuilder.addApproverId( PrincipalKey.from( approverId ) ) );
        }
        if ( publishRequest != null )
        {
           paramsBuilder.setPublishRequest( publishRequest.toRequest() );
        }
        this.createIssueParams = paramsBuilder.build();
    }

    @JsonIgnore
    public CreateIssueParams getCreateIssueParams()
    {
        return createIssueParams;
    }
}
