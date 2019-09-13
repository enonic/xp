package com.enonic.xp.repo.impl.storage;

import java.util.ArrayList;
import java.util.List;

public class GetByIdsRequest
{
    private final List<GetByIdRequest> requests = new ArrayList<>();

    public GetByIdsRequest add( final GetByIdRequest request )
    {
        this.requests.add( request );
        return this;
    }

    public List<GetByIdRequest> getRequests()
    {
        return requests;
    }
}