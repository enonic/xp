package com.enonic.xp.elasticsearch.client.impl;

import java.io.InputStream;

public class EsClientResponseBaseTest
{

    protected final InputStream getResource( final String fileName )
    {
        final InputStream inputStream = this.getClass().getResourceAsStream( fileName );

        if ( inputStream == null )
        {
            throw new IllegalArgumentException( "Resource [" + fileName + "] not found relative to: " + this.getClass() );
        }

        return inputStream;
    }

}
