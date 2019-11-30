package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;

public class ElasticsearchServer
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ElasticsearchServer.class );

    private Process process;

    private Thread outStreamReader;

    private final CountDownLatch startedLatch = new CountDownLatch( 1 );

    private final AtomicBoolean statedSuccessfully = new AtomicBoolean();

    private final Path esPathConf;

    private final Path esPathTmp;

    private final ElasticsearchInstaller installer;

    private ElasticsearchServer( final ElasticsearchServerBuilder builder )
    {
        this.esPathConf = builder.esPathConf;
        this.esPathTmp = builder.esPathTmp;
        this.installer = new ElasticsearchInstaller( builder.downloaderConfig );
    }

    public synchronized void start()
        throws IOException
    {
        installer.install();
        copyConfigFiles();
        startElasticProcess();
        installExitHook();
        try
        {
            startedLatch.await();
        }
        catch ( InterruptedException e )
        {
            throw new IOException( e );
        }
    }

    private void copyConfigFiles()
        throws IOException
    {
        copyConfigFile( "jvm.options" );
        copyConfigFile( "log4j2.properties" );
    }

    private void copyConfigFile( String filename )
        throws IOException
    {
        Files.copy( ElasticsearchConstants.ES_CONFIG_EXTRACTED_PATH.resolve( filename ), esPathConf.resolve( filename ) );
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
        final ProcessBuilder processBuilder = new ProcessBuilder( ElasticsearchConstants.ES_EXECUTABLE_PATH.toString() ).
            redirectErrorStream( true );
        final Map<String, String> environment = processBuilder.environment();
        environment.put( "ES_PATH_CONF", esPathConf.toAbsolutePath().toString() );
        environment.put( "ES_TMPDIR", esPathTmp.toAbsolutePath().toString() );

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
        private Path esPathConf;

        private Path esPathTmp;

        private ElasticsearchDownloaderConfig downloaderConfig;

        public static ElasticsearchServerBuilder builder()
        {
            return new ElasticsearchServerBuilder();
        }

        public ElasticsearchServerBuilder esPathConf( final Path esPathConf )
        {
            this.esPathConf = esPathConf;
            return this;
        }

        public ElasticsearchServerBuilder esPathTmp( final Path esPathTmp )
        {
            this.esPathTmp = esPathTmp;
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
}

