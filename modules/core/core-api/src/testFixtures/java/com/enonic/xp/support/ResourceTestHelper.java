package com.enonic.xp.support;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
        try (final InputStream stream = url.openStream())
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
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
