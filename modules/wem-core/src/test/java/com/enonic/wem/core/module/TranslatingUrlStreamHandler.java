package com.enonic.wem.core.module;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.function.Function;

public final class TranslatingUrlStreamHandler
    implements URLStreamHandlerFactory
{
    private final String protocol;

    private final Function<String, File> translator;

    public TranslatingUrlStreamHandler( final String protocol, final Function<String, File> translator )
    {
        this.protocol = protocol;
        this.translator = translator;
    }

    @Override
    public URLStreamHandler createURLStreamHandler( final String protocol )
    {
        if ( !this.protocol.equals( protocol ) )
        {
            return null;
        }

        return new URLStreamHandler()
        {
            @Override
            protected URLConnection openConnection( final URL url )
                throws IOException
            {
                final File file = translator.apply( url.getPath() );
                return file.toURI().toURL().openConnection();
            }
        };
    }

    public void install()
    {
        URL.setURLStreamHandlerFactory( this );
    }
}
