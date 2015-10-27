package com.enonic.xp.repo.impl.storage;

import java.util.List;

import com.google.common.collect.Lists;

public class GetByIdsRequest
{
    private final List<GetByIdRequest> requests = Lists.newLinkedList();

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