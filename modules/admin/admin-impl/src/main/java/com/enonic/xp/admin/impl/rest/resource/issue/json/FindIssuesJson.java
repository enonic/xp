package com.enonic.xp.admin.impl.rest.resource.issue.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.issue.FindIssuesParams;
import com.enonic.xp.issue.IssueStatus;

public class FindIssuesJson
{
    private final FindIssuesParams findIssuesParams;

    @JsonCreator
    public FindIssuesJson( @JsonProperty("contentIds") final List<String> contentIds, @JsonProperty("status") final String status,
                           @JsonProperty("from") final Integer from, @JsonProperty("size") final Integer size )
    {

        final FindIssuesParams.Builder paramsBuilder = FindIssuesParams.create().
            from( from ).
            size( size );

        if ( contentIds != null )
        {
            final ContentIds.Builder builder = ContentIds.create();
            for ( String id : contentIds )
            {
                builder.add( ContentId.from( id ) );
            }
            paramsBuilder.items( builder.build() );
        }
        if ( status != null )
        {
            paramsBuilder.status( IssueStatus.valueOf( status ) );
        }

        findIssuesParams = paramsBuilder.build();
    }

    public FindIssuesParams getFindIssuesParams()
    {
        return findIssuesParams;
    }
}
