package com.enonic.xp.admin.impl.json.content;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;

public class CompareContentResultsJson
{
    private final Set<CompareContentResultJson> compareContentResults = Sets.newHashSet();

    public CompareContentResultsJson( final CompareContentResults compareContentResults,
                                      final GetPublishStatusResultsJson getPublishStatusResultsJson )
    {
        for ( final CompareContentResult compareContentResult : compareContentResults )
        {
            PublishStatus publishStatus = getPublishStatusResultsJson.getGetPublishStatusResults().stream().
                filter( publishStatusJson -> publishStatusJson.getId().equals( compareContentResult.getContentId().toString() ) ).
                map( publishStatusJson -> publishStatusJson.getPublishStatus() ).
                findFirst().
                orElse( null );

            this.compareContentResults.add( new CompareContentResultJson( compareContentResult, publishStatus ) );
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public Set<CompareContentResultJson> getCompareContentResults()
    {
        return compareContentResults;
    }
}
