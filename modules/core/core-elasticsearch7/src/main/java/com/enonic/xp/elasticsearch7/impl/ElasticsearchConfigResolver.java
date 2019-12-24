package com.enonic.xp.elasticsearch7.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.xp.home.HomeDir;

import static com.google.common.base.Strings.nullToEmpty;

public final class ElasticsearchConfigResolver
{

    private static final String XP_HOME_PATTERN = "\\$\\{xp.home}";

    private static final String XP_HOME = "${xp.home}";

    private final ElasticsearchServerConfig configuration;

    public ElasticsearchConfigResolver( final ElasticsearchServerConfig configuration )
    {
        this.configuration = configuration;
    }

    public ElasticsearchSettings resolve()
    {
        final ElasticsearchConfigValidator validator = new ElasticsearchConfigValidator();
        validator.validate();

        final ElasticsearchSettings.Builder builder = ElasticsearchSettings.builder().
            pathConf( resolvePathConfDir() ).
            pathData( resolvePathDataDir() ).
            pathLogs( resolvePathLogsDir() ).
            pathRepo( resolvePathRepoDir() ).
            pathWork( resolvePathWorkDir() ).
            gatewayExpectedNodes( configuration.gateway_expectedNodes() ).
            gatewayRecoverAfterNodes( configuration.gateway_recoverAfterNodes() ).
            gatewayRecoverAfterTime( configuration.gateway_recoverAfterTime() ).
            clusterRoutingAllocationDiskThresholdEnabled( configuration.cluster_routing_allocation_disk_thresholdEnabled() ).
            clusterName( configuration.cluster_name() ).
            indexMaxResultWindow( configuration.index_maxResultWindow() ).
            httpPort( configuration.http_port() ).
            transportPort( configuration.transport_port() );

        return builder.build();
    }

    public String resolveElasticServerDir()
    {
        return resolveParameterWithXpHome( "esServerDir", configuration.esServerDir() );
    }

    public String resolvePathConfDir()
    {
        return resolveParameterWithXpHome( "path.conf", configuration.path_conf() );
    }

    public String resolvePathRepoDir()
    {
        return resolveParameterWithXpHome( "path.repo", configuration.path_repo() );
    }

    public String resolvePathWorkDir()
    {
        return resolveParameterWithXpHome( "path.work", configuration.path_work() );
    }

    public String resolvePathDataDir()
    {
        return resolveParameterWithXpHome( "path.data", configuration.path_data() );
    }

    public String resolvePathLogsDir()
    {
        return resolveParameterWithXpHome( "path.logs", configuration.path_logs() );
    }

    private String resolveParameterWithXpHome( final String parameter, final String value )
    {
        if ( nullToEmpty( value ).isBlank() )
        {
            throw new IllegalArgumentException( String.format( "The \"%s\" property must be defined.", parameter ) );
        }

        if ( value.contains( XP_HOME ) )
        {
            return value.replaceAll( XP_HOME_PATTERN, HomeDir.get().toString() );
        }

        return value;
    }

    private class ElasticsearchConfigValidator
    {

        private void validate()
        {
            ensureCorrectSdkMode().
                ensureCorrectElasticServerDir();

            ensureCorrectPathConfDir().
                ensureCorrectPathRepoDir().
                ensureCorrectPathWorkDir().
                ensureCorrectPathDataDir().
                ensureCorrectPathLogsDir();

            ensureCorrectHttpPort().
                ensureCorrectTransportPort().
                ensureCorrectClusterName().
                ensureCorrectGatewayRecoverAfterTime().
                ensureCorrectGatewayExpectedNodes().
                ensureCorrectGatewayRecoverAfterNodes().
                ensureCorrectIndexMaxResultWindow();
        }

        ElasticsearchConfigValidator ensureCorrectSdkMode()
        {
            if ( !configuration.embeddedMode() )
            {
                throw new IllegalArgumentException( "The SDK mode is disabled" );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectElasticServerDir()
        {
            validateCorrectDir( "esServerDir", resolveElasticServerDir() );

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectPathConfDir()
        {
            validateCorrectDir( "path.conf", resolvePathConfDir() );

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectPathDataDir()
        {
            validateCorrectDir( "path.data", resolvePathDataDir() );

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectPathRepoDir()
        {
            validateCorrectDir( "path.repo", resolvePathRepoDir() );

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectPathWorkDir()
        {
            validateCorrectDir( "path.work", resolvePathWorkDir() );

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectPathLogsDir()
        {
            validateCorrectDir( "path.logs", resolvePathLogsDir() );

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectHttpPort()
        {
            if ( nullToEmpty( configuration.path_conf() ).isBlank() )
            {
                throw new IllegalArgumentException( "The \"http.port\" parameter is required." );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectClusterName()
        {
            if ( nullToEmpty( configuration.cluster_name() ).isBlank() )
            {
                throw new IllegalArgumentException( "The \"cluster.name\" parameter is required." );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectTransportPort()
        {
            if ( nullToEmpty( configuration.transport_port() ).isBlank() )
            {
                throw new IllegalArgumentException( "The \"transport.port\" parameter is required." );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectGatewayRecoverAfterTime()
        {
            if ( nullToEmpty( configuration.gateway_recoverAfterTime() ).isBlank() )
            {
                throw new IllegalArgumentException( "The \"gateway_RecoverAfterTime\" parameter is required." );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectGatewayExpectedNodes()
        {
            if ( configuration.gateway_expectedNodes() < 0 )
            {
                throw new IllegalArgumentException( "The \"gateway_ExpectedNodes\" must be positive" );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectGatewayRecoverAfterNodes()
        {
            if ( configuration.gateway_expectedNodes() < 0 )
            {
                throw new IllegalArgumentException( "The \"gateway_RecoverAfterNodes\" must be positive" );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectIndexMaxResultWindow()
        {
            if ( configuration.gateway_expectedNodes() < 0 )
            {
                throw new IllegalArgumentException( "The \"index_MaxResultWindow\" must be positive" );
            }

            return this;
        }

        private void validateCorrectDir( final String parameter, final String value )
        {
            final Path directory = Paths.get( value );

            if ( Files.notExists( directory ) )
            {
                throw new IllegalArgumentException( String.format( "Directory  \"%s\" not found.", value ) );
            }

            if ( !Files.isDirectory( directory ) )
            {
                throw new IllegalArgumentException( String.format( "The \"%s\" must be pointed to directory.", parameter ) );
            }
        }

    }

}
