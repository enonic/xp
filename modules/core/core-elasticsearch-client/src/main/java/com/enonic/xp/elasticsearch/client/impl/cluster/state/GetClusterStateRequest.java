package com.enonic.xp.elasticsearch.client.impl.cluster.state;

import org.apache.http.client.methods.HttpGet;
import org.elasticsearch.client.Request;
import org.elasticsearch.common.unit.TimeValue;

public class GetClusterStateRequest
{

    private final Request request;

    public GetClusterStateRequest()
    {
        this.request = new Request( HttpGet.METHOD_NAME, "_cluster/state" );
    }

    public GetClusterStateRequest( final String timeout )
    {
        this.request = new Request( HttpGet.METHOD_NAME, String.format( "_cluster/state?master_timeout=%s", timeout ) );
    }

    public GetClusterStateRequest( final TimeValue timeout )
    {
        this.request = new Request( HttpGet.METHOD_NAME, String.format( "_cluster/state?master_timeout=%s", timeout.getStringRep() ) );
    }

    public Request getInternalRequest()
    {
        return request;
    }

}
