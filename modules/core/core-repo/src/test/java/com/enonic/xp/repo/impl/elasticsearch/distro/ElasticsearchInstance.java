package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;

import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.ROOT_DATA_DIR;
import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.TMP_ELASTICSEARCH_DIR;

public enum ElasticsearchInstance
{

    INSTANCE;

    private ElasticsearchServer elasticsearchServer;

    private File snapshotsDir;

    ElasticsearchInstance()
    {
        elasticsearchServer = ElasticsearchServer.ElasticsearchServerBuilder.builder().
            esJavaOpts( "-Xms512m -Xmx512m" ).
            destroyInstallationDirectoryOnStop( false ).
            downloaderConfig( ElasticsearchDownloaderConfig.builder().build() ).
            build();

        initialize();
    }

    public void initialize()
    {
        try
        {
            Path rootDirectory = Files.createTempDirectory( TMP_ELASTICSEARCH_DIR );

            System.setProperty( "mapper.allow_dots_in_name", "true" );

            final File dataDir = new File( rootDirectory.toFile(), ROOT_DATA_DIR );

            final File pathHome = new File( dataDir, "index" );
            FileUtils.forceMkdir( pathHome );

            final File pathData = new File( dataDir, "data" );
            FileUtils.forceMkdir( pathData );

            snapshotsDir = new File( dataDir, "repo" );
            FileUtils.forceMkdir( snapshotsDir );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public void start()
        throws IOException, InterruptedException
    {
        if ( !elasticsearchServer.isStarted() )
        {
            elasticsearchServer.start();
        }
    }

    public void stop()
    {
        if ( elasticsearchServer.isStarted() )
        {
            elasticsearchServer.stop();
        }
    }

    public File getSnapshotsDir()
    {
        return snapshotsDir;
    }

}
