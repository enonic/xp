package com.enonic.xp.core.impl.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.event.Event;

public class ApplicationLoader
{
    private final Consumer<Event> eventConsumer;

    public ApplicationLoader( final Consumer<Event> eventConsumer )
    {
        this.eventConsumer = eventConsumer;
    }

    public ByteSource load( URL url )
    {
        try
        {
            URLConnection connection = url.openConnection();

            ByteArrayOutputStream os = null;

            try (final InputStream is = connection.getInputStream())
            {
                int totalLength = connection.getContentLength();
                int bytesRead;
                float totalRead = 0;
                int lastPct = 0;
                int currentPct;
                byte[] buffer = new byte[8192];
                os = new ByteArrayOutputStream();

                while ( ( bytesRead = is.read( buffer ) ) != -1 )
                {
                    os.write( buffer, 0, bytesRead );
                    totalRead += bytesRead;

                    currentPct = (int) ( ( totalRead / totalLength ) * 100 );

                    if ( lastPct != currentPct )
                    {
                        eventConsumer.accept( ApplicationEvents.progress( url.toString(), currentPct ) );
                        lastPct = currentPct;
                    }
                }
                os.flush();

                return ByteSource.wrap( os.toByteArray() );
            }
            finally
            {
                if ( os != null )
                {
                    os.close();
                }
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load application from " + url );
        }
    }
}
