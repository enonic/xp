package com.enonic.xp.elasticsearch.server.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.elasticsearch.server.config.ElasticsearchServerConfig;
import com.enonic.xp.elasticsearch.server.config.ElasticsearchServerConfigResolver;

@Component(immediate = true, configurationPid = "com.enonic.xp.elasticsearch")
public class ElasticsearchServerActivator
{
    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchServerActivator.class );

    private Path elasticServerDir;

    private Path elasticEmbeddedConfigDir;

    private Path elasticsearchYaml;

    private Path elasticWorkDir;

    private Process process;

    private Thread outStreamReader;

    private final CountDownLatch startedLatch = new CountDownLatch( 1 );

    private final AtomicBoolean statedSuccessfully = new AtomicBoolean();

    @Activate
    public ElasticsearchServerActivator( final ElasticsearchServerConfig elasticsearchConfig )
    {
        if ( elasticsearchConfig.embeddedMode() )
        {
            final ElasticsearchServerConfigResolver resolver = new ElasticsearchServerConfigResolver( elasticsearchConfig );

            this.elasticServerDir = Paths.get( resolver.resolveElasticServerDir() );
            this.elasticWorkDir = Paths.get( resolver.resolvePathWorkDir() );
            this.elasticEmbeddedConfigDir = Paths.get( resolver.resolvePathConfDir() );
            this.elasticsearchYaml = elasticEmbeddedConfigDir.resolve( "elasticsearch.yml" );

            final ElasticsearchServerSettings elasticsearchSettings = resolver.resolve();
            elasticsearchSettings.writeToYml( elasticsearchYaml );

            copyElasticsearchConfiguration();
            startElasticProcess();
        }
    }

    protected final Path getElasticServerDir()
    {
        return elasticServerDir;
    }

    private void startElasticProcess()
    {
        final ProcessBuilder processBuilder =
            new ProcessBuilder( getElasticServerDir().resolve( "bin" ).resolve( executableFilename() ).toString() ).
                redirectErrorStream( true );

        processBuilder.environment().put( "ES_PATH_CONF", elasticsearchYaml.getParent().toAbsolutePath().toString() );
        if ( Files.exists( elasticWorkDir ) )
        {
            processBuilder.environment().put( "ES_TMPDIR", elasticWorkDir.toAbsolutePath().toString() );
        }

        try
        {
            process = processBuilder.start();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

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

    private void copyElasticsearchConfiguration()
    {
        copyConfigFile( "jvm.options" );
        copyConfigFile( "log4j2.properties" );
    }

    private void copyConfigFile( final String filename )
    {
        try
        {
            Files.copy( getElasticServerDir().resolve( "config" ).resolve( filename ), elasticEmbeddedConfigDir.resolve( filename ),
                        StandardCopyOption.REPLACE_EXISTING );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

    }

    @Deactivate
    public void deactivate()
        throws InterruptedException
    {
        startedLatch.await();
        outStreamReader.interrupt();
        process.children().forEach( ProcessHandle::destroy );
        process.destroy();
        process.waitFor( 1, TimeUnit.MINUTES );
    }

}
