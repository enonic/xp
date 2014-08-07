package com.enonic.wem.admin.json.content;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.CompareContentResult;
import com.enonic.wem.api.content.CompareContentResults;

public class CompareContentResultsJson
{
    private final Set<CompareContentResultJson> compareContentResults = Sets.newHashSet();

    public CompareContentResultsJson( final CompareContentResults compareContentResults )
    {
        for ( final CompareContentResult compareContentResult : compareContentResults )
        {
            this.compareContentResults.add( new CompareContentResultJson( compareContentResult ) );
        }
    }

    @SuppressWarnings( "UnusedDeclaration" )
    public Set<CompareContentResultJson> getCompareContentResults()
    {
        return compareContentResults;
    }
}
