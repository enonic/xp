package com.enonic.wem.api.support;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ResourceTestHelper
{
    private final Object testInstance;

    public ResourceTestHelper( final Object testInstance )
    {
        this.testInstance = testInstance;
    }

    public String loadTestFile( final String fileName )
    {
        URL url = getResource( testInstance.getClass().getSimpleName() + "-" + fileName );
        try
        {
            Path path = Paths.get( url.toURI() );
            final List<String> lines = Files.readAllLines( path, Charset.forName( "UTF-8" ) );
            final StringBuilder s = new StringBuilder();
            for ( final String line : lines )
            {
                s.append( line ).append( "\n" );
            }
            return s.toString();
        }
        catch ( URISyntaxException | IOException e )
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
