package com.enonic.xp.repo.impl.storage;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.repo.impl.SearchPreference;

public class GetByIdsRequest
{
    private final List<GetByIdRequest> requests = new ArrayList<>();

    private final SearchPreference searchPreference;

    public GetByIdsRequest( final SearchPreference searchPreference )
    {
        this.searchPreference =  searchPreference;
    }

    public GetByIdsRequest add( final GetByIdRequest request )
    {
        this.requests.add( request );
        return this;
    }

    public List<GetByIdRequest> getRequests()
    {
        return requests;
    }

    public SearchPreference getSearchPreference()
    {
        return searchPreference;
    }
}
