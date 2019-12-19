package com.enonic.xp.elasticsearch7.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

import static com.google.common.base.Strings.nullToEmpty;

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
        throws IOException
    {
        if ( elasticsearchConfig.embeddedMode() )
        {
            validateIfEmbeddedModeEnabled( elasticsearchConfig );

            prepareElasticsearch( elasticsearchConfig );
            startElasticProcess();
        }
    }

    private void validateIfEmbeddedModeEnabled( final ElasticsearchServerConfig elasticsearchConfig )
    {
        if ( nullToEmpty( elasticsearchConfig.esServerDir() ).isBlank() )
        {
            throw new IllegalArgumentException( "The 'esServerDir' property must be defined." );
        }

        this.elasticServerDir = Paths.get( elasticsearchConfig.esServerDir() );

        if ( Files.notExists( elasticServerDir ) || !Files.isDirectory( elasticServerDir ) )
        {
            throw new IllegalArgumentException( "The 'esServerDir' must be a directory" );
        }

        if ( nullToEmpty( elasticsearchConfig.path_conf() ).isBlank() )
        {
            throw new IllegalArgumentException( "The 'path.conf' property must be defined." );
        }

        this.elasticEmbeddedConfigDir = Paths.get( elasticsearchConfig.path_conf() );

        if ( Files.notExists( elasticEmbeddedConfigDir ) || !Files.isDirectory( elasticEmbeddedConfigDir ) )
        {
            throw new IllegalArgumentException( "The 'path.conf' must be pointed to a directory" );
        }

        if ( !nullToEmpty( elasticsearchConfig.path_work() ).isBlank() )
        {
            this.elasticWorkDir = Paths.get( elasticsearchConfig.path_work() );

            if ( Files.notExists( elasticWorkDir ) || !Files.isDirectory( elasticWorkDir ) )
            {
                throw new IllegalArgumentException( "The 'path.work' must be pointed to a directory" );
            }
        }
    }

    private void startElasticProcess()
        throws IOException
    {
        final ProcessBuilder processBuilder =
            new ProcessBuilder( elasticServerDir.resolve( "bin" ).resolve( executableFilename() ).toString() ).
                redirectErrorStream( true );

        processBuilder.environment().put( "ES_PATH_CONF", elasticsearchYaml.getParent().toAbsolutePath().toString() );
        if ( Files.exists( elasticWorkDir ) )
        {
            processBuilder.environment().put( "ES_TMPDIR", elasticWorkDir.toAbsolutePath().toString() );
        }

        process = processBuilder.start();

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

    private void prepareElasticsearch( final ElasticsearchServerConfig elasticsearchConfig )
        throws IOException
    {
        copyConfigFile( "jvm.options" );
        copyConfigFile( "log4j2.properties" );

        prepareElasticsearchYamlFile( elasticsearchConfig );
    }

    private void copyConfigFile( final String filename )
        throws IOException
    {
        Files.copy( elasticServerDir.resolve( "config" ).resolve( filename ), elasticEmbeddedConfigDir.resolve( filename ),
                    StandardCopyOption.REPLACE_EXISTING );
    }

    private void prepareElasticsearchYamlFile( final ElasticsearchServerConfig elasticsearchConfig )
        throws IOException
    {
        this.elasticsearchYaml = elasticEmbeddedConfigDir.resolve( "elasticsearch.yml" );

        try (final BufferedWriter writer = Files.newBufferedWriter( elasticsearchYaml ))
        {
            writeProperty( "http.port", elasticsearchConfig.http_port(), writer );
            writeProperty( "path", elasticsearchConfig.path(), writer );
            writeProperty( "path.home", elasticsearchConfig.path_home(), writer );
            writeProperty( "path.data", elasticsearchConfig.path_data(), writer );
            writeProperty( "path.repo", elasticsearchConfig.path_repo(), writer );
            writeProperty( "path.logs", elasticsearchConfig.path_logs(), writer );
            writeProperty( "path.plugins", elasticsearchConfig.path_plugins(), writer );
            writeProperty( "cluster.name", elasticsearchConfig.cluster_name(), writer );
            writeProperty( "cluster.routing.allocation.disk.threshold_enabled",
                           elasticsearchConfig.cluster_routing_allocation_disk_thresholdEnabled(), writer );
            writeProperty( "transport.port", elasticsearchConfig.transport_port(), writer );
            writeProperty( "gateway.expected_nodes", elasticsearchConfig.gateway_expectedNodes(), writer );
            writeProperty( "gateway.recover_after_time", elasticsearchConfig.gateway_recoverAfterTime(), writer );
            writeProperty( "gateway.recover_after_nodes", elasticsearchConfig.gateway_recoverAfterNodes(), writer );

            writeProperty( "discovery.type", "single-node", writer );
        }
    }

    private void writeProperty( final String property, final Object value, final BufferedWriter writer )
        throws IOException
    {
        if ( value != null )
        {
            final Object normalizedValue = value instanceof String ? "'" + value + "'" : value;

            writer.write( property + ": " + normalizedValue );
            writer.newLine();
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
