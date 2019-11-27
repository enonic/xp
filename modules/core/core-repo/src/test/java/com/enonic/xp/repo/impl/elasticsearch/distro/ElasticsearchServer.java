package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;

import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.EXTRACTED_ARCHIVE_NAME;


public class ElasticsearchServer
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ElasticsearchServer.class );

    private Process process;

    private Thread outStreamReader;

    private final CountDownLatch startedLatch = new CountDownLatch( 1 );

    private final AtomicBoolean statedSuccessfully = new AtomicBoolean();

    private final String esPathConf;


    private final ElasticsearchInstaller installer;

    private ElasticsearchServer( final ElasticsearchServerBuilder builder )
    {
        this.esPathConf = builder.esPathConf;
        this.installer = new ElasticsearchInstaller( builder.downloaderConfig );
    }

    public synchronized void start()
        throws InterruptedException, IOException
    {
        installer.install();
        startElasticProcess();
        installExitHook();
        startedLatch.await();
    }

    public synchronized void stop()
    {
        try
        {
            stopElasticServer();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public boolean isStarted()
    {
        return startedLatch.getCount() == 0;
    }

    private void installExitHook()
    {
        Runtime.getRuntime().addShutdownHook( new Thread( this::stop, "ElsInstanceCleaner" ) );
    }


    public void startElasticProcess()
        throws IOException
    {
        final ProcessBuilder processBuilder = new ProcessBuilder(
            Path.of( installer.getInstallationDirectory().getPath(), EXTRACTED_ARCHIVE_NAME, "bin", executableFilename() ).toString() ).
            redirectErrorStream( true );
        processBuilder.environment().put( "ES_PATH_CONF", esPathConf );

        process = processBuilder.start();

        outStreamReader = new Thread( () -> {

            try (final BufferedReader in = new BufferedReader( new InputStreamReader( process.getInputStream() ) ))
            {
                String line;
                while ( ( line = in.readLine() ) != null )
                {
                    LOGGER.info( line );
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

    private void stopElasticServer()
        throws IOException, InterruptedException
    {
        LOGGER.info( "Stopping elasticsearch server..." );
        deactivate();
    }

    public static class ElasticsearchServerBuilder
    {
        private String esPathConf;

        private ElasticsearchDownloaderConfig downloaderConfig;

        public static ElasticsearchServerBuilder builder()
        {
            return new ElasticsearchServerBuilder();
        }

        public ElasticsearchServerBuilder esPathConf( final String esPathConf )
        {
            this.esPathConf = esPathConf;
            return this;
        }

        public ElasticsearchServerBuilder downloaderConfig( final ElasticsearchDownloaderConfig downloaderConfig )
        {
            this.downloaderConfig = downloaderConfig;
            return this;
        }

        public ElasticsearchServer build()
        {
            return new ElasticsearchServer( this );
        }
    }

    public void deactivate()
        throws IOException, InterruptedException
    {
        startedLatch.await();
        outStreamReader.interrupt();
        process.children().forEach( ProcessHandle::destroy );
        process.destroy();
        process.waitFor( 1, TimeUnit.MINUTES );
    }

    private static String executableFilename()
    {
        return "elasticsearch" + ( isWindows() ? ".bat" : "" );
    }

    private static boolean isWindows()
    {
        return System.getProperty( "os.name" ).startsWith( "Windows" );
    }

}

