package com.enonic.xp.elasticsearch7.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class Elasticsearch7Activator
{
    private final static Logger LOG = LoggerFactory.getLogger( Elasticsearch7Activator.class );

    private Process process;

    private Thread outStreamReader;

    private final CountDownLatch startedLatch = new CountDownLatch( 1 );

    private final AtomicBoolean statedSuccessfully = new AtomicBoolean();

    @Activate
    public void activate( final BundleContext context, final Map<String, String> map )
        throws IOException, InterruptedException
    {
        startElasticProcess();
    }

    private void startElasticProcess()
        throws IOException, InterruptedException
    {
        final String basePath = "\\es\\elasticsearch-2.4.6\\bin";
        process = new ProcessBuilder( Path.of( basePath, executableFilename() ).toString() ).
            redirectErrorStream( true ).
            start();

        outStreamReader = new Thread( () -> {

            try (final BufferedReader in = new BufferedReader( new InputStreamReader( process.getInputStream() ) ))
            {
                String line;
                while ( ( line = in.readLine() ) != null )
                {
                    LOG.info( line );
                    if ( line.endsWith( "] started" ) )
                    {
                        statedSuccessfully.set( true );
                        startedLatch.countDown();
                    }
                    if ( Thread.interrupted() )
                    {
                        return;
                    }
                }

            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
            finally
            {
                startedLatch.countDown();
            }
        } );
        outStreamReader.start();
    }


    private String executableFilename()
    {
        return "elasticsearch" + ( isWindows() ? ".bat" : "" );
    }

    private static boolean isWindows()
    {
        return System.getProperty( "os.name" ).startsWith( "Windows" );
    }

    @Deactivate
    public void deactivate()
        throws IOException, InterruptedException
    {
        startedLatch.await();
        outStreamReader.interrupt();
        process.children().forEach( ProcessHandle::destroy );
        process.destroy();
        process.waitFor( 1, TimeUnit.MINUTES );
    }
}
