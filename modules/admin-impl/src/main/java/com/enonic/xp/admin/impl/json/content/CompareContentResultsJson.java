package com.enonic.xp.admin.impl.json.content;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.core.content.CompareContentResult;
import com.enonic.xp.core.content.CompareContentResults;

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
