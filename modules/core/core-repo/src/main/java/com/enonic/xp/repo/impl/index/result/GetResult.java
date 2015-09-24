package com.enonic.xp.repo.impl.index.result;

import com.enonic.xp.index.IndexPath;

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

    public String getStringValue( final IndexPath indexPath, final boolean failOnMissing )
    {
        final SearchResultFieldValue fieldValue = this.result.getField( indexPath.getPath(), failOnMissing );

        if ( fieldValue == null )
        {
            return null;
        }

        return fieldValue.getValue() != null ? fieldValue.getValue().toString() : null;
    }
}
