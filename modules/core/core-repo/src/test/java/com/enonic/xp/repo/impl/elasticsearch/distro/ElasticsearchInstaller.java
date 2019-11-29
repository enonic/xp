package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;


class ElasticsearchInstaller
{

    private static final Logger LOGGER = LoggerFactory.getLogger( ElasticsearchInstaller.class );

    private final ElasticsearchDownloader downloader;

    ElasticsearchInstaller( final ElasticsearchDownloaderConfig downloaderConfig )
    {
        this.downloader = new ElasticsearchDownloader( downloaderConfig );
    }

    public void install()
        throws IOException
    {
        if ( Files.exists( ElasticsearchConstants.ES_EXECUTABLE_PATH ) )
        {
            LOGGER.info( "Skip Elasticsearch installation as it is already installed: {}", ElasticsearchConstants.ES_EXECUTABLE_PATH );
            return;
        }

        Files.createDirectories( ElasticsearchConstants.ES_DIR );

        downloader.download();

        LOGGER.info( "Installing Elasticsearch into {}", getInstallationDirectory() );

        final Path source = ElasticsearchConstants.ES_DIR.resolve( ElasticsearchArtifact.getArchiveNameByOS() );
        unzip( source );
        LOGGER.info( "Completed Elasticsearch installation" );
    }

    private void unzip( Path downloadedTo )
        throws IOException
    {
        Archiver archiver = SystemUtils.IS_OS_WINDOWS
            ? ArchiverFactory.createArchiver( ArchiveFormat.ZIP )
            : ArchiverFactory.createArchiver( ArchiveFormat.TAR, CompressionType.GZIP );
        archiver.extract( downloadedTo.toFile(), ElasticsearchConstants.ES_DIR.toFile() );
    }

    public File getInstallationDirectory()
    {
        return ElasticsearchConstants.ES_DIR.toFile();
    }
}
