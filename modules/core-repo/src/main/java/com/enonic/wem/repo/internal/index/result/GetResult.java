package com.enonic.wem.repo.internal.index.result;

public class GetResult
{
    private final SearchResultEntry result;

    public GetResult( final SearchResultEntry result )
    {
        this.result = result;
    }

    public static GetResult empty()
    {
        return new GetResult( null );
    }

    public boolean isEmpty()
    {
        return result == null;
    }

    public SearchResultEntry getSearchResult()
    {
        return result;
    }
}
