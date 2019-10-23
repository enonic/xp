package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;

import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.ES_DIR;
import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.EXTRACTED_ARCHIVE_NAME;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ElasticsearchServer
{

    private static final Logger logger = LoggerFactory.getLogger( ElasticsearchServer.class );

    private boolean started;

    private final Object startedLock = new Object();

    private Process elastic;

    private Thread ownerThread;

    private final long startTimeoutInMs = 15000;

    private volatile int pid = -1;

    private final String esJavaOpts;

    private final boolean destroyInstallationDirectoryOnStop;

    private final ElasticsearchDownloaderConfig downloaderConfig;

    private ElasticsearchServer( final ElasticsearchServerBuilder builder )
    {
        this.esJavaOpts = builder.esJavaOpts;
        this.destroyInstallationDirectoryOnStop = builder.destroyInstallationDirectoryOnStop;
        this.downloaderConfig = builder.downloaderConfig;
    }

    public synchronized void start()
        throws InterruptedException, IOException
    {
        ElasticsearchInstaller installer = new ElasticsearchInstaller( downloaderConfig );
        installer.install();

        startElasticProcess();
        installExitHook();
        waitForElasticToStart();
    }

    public synchronized void stop()
    {
        try
        {
            stopElasticServer();
            finalizeClose();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public boolean isStarted()
    {
        return started;
    }

    private void deleteInstallationDirectory()
    {
        try
        {
            FileUtils.deleteDirectory( ES_DIR );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( "Could not delete data directory of elasticsearch server. Possibly an instance is running.",
                                            e );
        }
    }

    private void startElasticProcess()
    {
        ownerThread = new Thread( () -> {
            try
            {
                synchronized ( this )
                {
                    ProcessBuilder builder = new ProcessBuilder();
                    builder.environment().put( "ES_JAVA_OPTS", esJavaOpts );
                    builder.redirectErrorStream( true );
                    builder.command( elasticExecutable() );
                    elastic = builder.start();
                }

                try (final BufferedReader outputStream = new BufferedReader( new InputStreamReader( elastic.getInputStream(), UTF_8 ) ))
                {
                    String line;
                    while ( ( line = readLine( outputStream ) ) != null )
                    {
                        logger.info( line );
                        parseElasticLogLine( line );
                    }
                }
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
        }, "EmbeddedElsHandler" );
        ownerThread.start();
    }

    private String readLine( BufferedReader outputStream )
    {
        try
        {
            return outputStream.readLine();
        }
        catch ( IOException e )
        {
            return null;
        }
    }

    private void installExitHook()
    {
        Runtime.getRuntime().addShutdownHook( new Thread( this::stop, "ElsInstanceCleaner" ) );
    }

    private String elasticExecutable()
    {
        String suffix = EXTRACTED_ARCHIVE_NAME + File.separator + "bin/elasticsearch";

        if ( SystemUtils.IS_OS_WINDOWS )
        {
            suffix += ".bat";
        }

        return ES_DIR.getAbsolutePath() + File.separator + suffix;
    }

    private void waitForElasticToStart()
        throws InterruptedException
    {
        logger.info( "Waiting for ElasticSearch to start..." );
        long waitUntil = System.currentTimeMillis() + startTimeoutInMs;

        synchronized ( startedLock )
        {
            boolean timedOut = false;
            while ( !started && !timedOut && ( elastic == null || elastic.isAlive() ) )
            {
                startedLock.wait( 100 );
                timedOut = System.currentTimeMillis() > waitUntil;
            }
            if ( !started )
            {
                String message = timedOut
                    ? "Failed to start elasticsearch within time-out"
                    : "Failed to start elasticsearch. Check previous logs for details";
                throw new RuntimeException( message );
            }
        }

        logger.info( "ElasticSearch started..." );
    }

    private void parseElasticLogLine( String line )
    {
        if ( started )
        {
            return;
        }
        if ( line.contains( "] started" ) )
        {
            signalElasticStarted();
        }
        else if ( line.contains( ", pid[" ) )
        {
            tryExtractPid( line );
        }
    }

    private void signalElasticStarted()
    {
        synchronized ( startedLock )
        {
            started = true;
            startedLock.notifyAll();
        }
    }

    private void tryExtractPid( String line )
    {
        Matcher matcher = Pattern.compile( "pid\\[(\\d+)]" ).matcher( line );
        if ( !matcher.find() )
        {
            throw new RuntimeException( "Could not extract Pid" );
        }
        pid = Integer.parseInt( matcher.group( 1 ) );
        logger.info( "Detected Elasticsearch PID : " + pid );
    }

    private void stopElasticServer()
        throws IOException, InterruptedException
    {
        logger.info( "Stopping elasticsearch server..." );
        if ( pid > -1 )
        {
            stopElasticGracefully();
        }
        pid = -1;
        if ( elastic != null )
        {
            int rc = elastic.waitFor();
            logger.info( "Elasticsearch exited with RC " + rc );
        }
        elastic = null;
        if ( ownerThread != null )
        {
            ownerThread.join();
        }
        ownerThread = null;
    }

    private void stopElasticGracefully()
        throws IOException
    {
        if ( SystemUtils.IS_OS_WINDOWS )
        {
            stopElasticOnWindows();
        }
        else
        {
            elastic.destroy();
        }
    }

    private void stopElasticOnWindows()
        throws IOException
    {
        Runtime.getRuntime().exec( "taskkill /f /pid " + pid );
    }

    private void finalizeClose()
    {
        if ( destroyInstallationDirectoryOnStop )
        {
            logger.info( "Removing installation directory..." );
            deleteInstallationDirectory();
        }

        logger.info( "Finishing..." );
        started = false;
    }

    public static class ElasticsearchServerBuilder
    {
        private String esJavaOpts;

        private boolean destroyInstallationDirectoryOnStop;

        private ElasticsearchDownloaderConfig downloaderConfig;

        public static ElasticsearchServerBuilder builder()
        {
            return new ElasticsearchServerBuilder();
        }

        public ElasticsearchServerBuilder esJavaOpts( final String esJavaOpts )
        {
            this.esJavaOpts = esJavaOpts;
            return this;
        }

        public ElasticsearchServerBuilder destroyInstallationDirectoryOnStop( final boolean destroyInstallationDirectoryOnStop )
        {
            this.destroyInstallationDirectoryOnStop = destroyInstallationDirectoryOnStop;
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

}
