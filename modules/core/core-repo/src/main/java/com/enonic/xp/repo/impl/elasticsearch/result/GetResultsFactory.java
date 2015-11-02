package com.enonic.xp.repo.impl.elasticsearch.result;

import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;

import com.enonic.xp.repo.impl.storage.GetResults;

public class GetResultsFactory
{
    public static GetResults create( final MultiGetResponse getResponse )
    {
        final MultiGetItemResponse[] responses = getResponse.getResponses();

        final GetResults getResults = new GetResults();

        for ( final MultiGetItemResponse response : responses )
        {
            getResults.add( GetResultFactory.create( response.getResponse() ) );
        }

        return getResults;
    }
}
