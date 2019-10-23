package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;

import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.ES_DIR;

class ElasticsearchInstaller
{

    private static final Logger logger = LoggerFactory.getLogger( ElasticsearchInstaller.class );

    private ElasticsearchDownloader downloader;

    ElasticsearchInstaller( final ElasticsearchDownloaderConfig downloaderConfig )
    {
        this.downloader = new ElasticsearchDownloader( downloaderConfig );
    }

    public void install()
        throws IOException
    {
        downloader.download();

        FileUtils.forceMkdir( getInstallationDirectory() );

        logger.info( "Installing Elasticsearch into the " + getInstallationDirectory() + "..." );

        try
        {
            final File source = new File( ES_DIR, ElasticsearchArtifact.getArchiveNameByOS() );
            unzip( source.toPath() );
            logger.info( "Done" );
        }
        catch ( IOException e )
        {
            logger.info( "Failure : " + e );
            throw new RuntimeException( e );
        }
    }

    private void unzip( Path downloadedTo )
        throws IOException
    {
        Archiver archiver = SystemUtils.IS_OS_WINDOWS
            ? ArchiverFactory.createArchiver( ArchiveFormat.ZIP )
            : ArchiverFactory.createArchiver( ArchiveFormat.TAR, CompressionType.GZIP );
        archiver.extract( downloadedTo.toFile(), ES_DIR );
    }

    private File getInstallationDirectory()
    {
        return FileUtils.getFile( ES_DIR );
    }

}
