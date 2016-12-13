package com.enonic.xp.admin.impl.json.content;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.GetPublishStatusResult;
import com.enonic.xp.content.GetPublishStatusesResult;

public class CompareContentResultsJson
{
    private final Set<CompareContentResultJson> compareContentResults = Sets.newHashSet();

    public CompareContentResultsJson( final CompareContentResults compareContentResults,
                                      final GetPublishStatusesResult getPublishStatusesResult )
    {
        final Map<ContentId, GetPublishStatusResult> getPublishStatusResultsMap = getPublishStatusesResult.getGetPublishStatusResultsMap();

        for ( final CompareContentResult compareContentResult : compareContentResults )
        {
            final GetPublishStatusResult getPublishStatusResult = getPublishStatusResultsMap.get( compareContentResult.getContentId() );

            this.compareContentResults.add( new CompareContentResultJson( compareContentResult, getPublishStatusResult ) );
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public Set<CompareContentResultJson> getCompareContentResults()
    {
        return compareContentResults;
    }
}
