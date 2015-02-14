package com.enonic.xp.core.support;

import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class ResourceTestHelper
{
    private final Object testInstance;

    public ResourceTestHelper( final Object testInstance )
    {
        this.testInstance = testInstance;
    }

    public String loadTestFile( final String fileName )
    {
        final URL url = getResource( testInstance.getClass().getSimpleName() + "-" + fileName );
        try
        {
            return Resources.toString( url, Charsets.UTF_8 );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( "Failed to load test file: " + url, e );
        }
    }

    public URL getTestResource( final String fileName )
    {
        return getResource( testInstance.getClass().getSimpleName() + "-" + fileName );
    }

    public URL getResource( final String fileName )
    {
        final URL resource = testInstance.getClass().getResource( fileName );
        if ( resource == null )
        {
            throw new IllegalArgumentException( "Resource [" + fileName + "] not found relative to: " + testInstance.getClass() );
        }
        return resource;
    }
}
