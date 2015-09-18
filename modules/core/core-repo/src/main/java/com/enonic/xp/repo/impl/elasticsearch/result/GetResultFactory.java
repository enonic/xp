package com.enonic.xp.repo.impl.elasticsearch.result;

import org.elasticsearch.action.get.GetResponse;

import com.enonic.xp.repo.impl.index.result.GetResult;

public class GetResultFactory
{
    public static GetResult create( final GetResponse getResponse )
    {

        if ( !getResponse.isExists() )
        {
            return GetResult.empty();
        }

        return new GetResult( SearchResultEntriesFactory.create( getResponse ) );
    }
}
