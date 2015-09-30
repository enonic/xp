package com.enonic.wem.repo.internal.elasticsearch;

class ElasticsearchDataException
    extends RuntimeException
{

    public ElasticsearchDataException( final String message )
    {
        super( message );
    }
}
