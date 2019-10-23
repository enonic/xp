package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;

import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.ES_DIR;

class ElasticsearchDownloader
{
    private static final Logger logger = LoggerFactory.getLogger( ElasticsearchDownloader.class );

    private static final String ELS_PACKAGE_STATUS_FILE_SUFFIX = "-downloaded";

    private final ElasticsearchDownloaderConfig downloaderConfig;

    public ElasticsearchDownloader( final ElasticsearchDownloaderConfig downloaderConfig )
    {
        this.downloaderConfig = downloaderConfig;
    }

    public void download()
        throws IOException
    {
        FileUtils.forceMkdir( ES_DIR );

        URL url = getDownloadUrl();
        String localFileName = constructLocalFileName( url );
        File target = new File( ES_DIR, localFileName );
        File statusFile = new File( target.getParentFile(), target.getName() + ELS_PACKAGE_STATUS_FILE_SUFFIX );
        removeBrokenDownload( target, statusFile );
        if ( !target.exists() )
        {
            proceedWithDownload( url, target, statusFile );
        }
        else if ( !statusFile.exists() && maybeDownloading( target ) )
        {
            waitForDownload( target, statusFile );
        }
        else if ( !statusFile.exists() )
        {
            throw new IOException( "Broken download. File '" + target + "' exits but status '" + statusFile + "' file wash not created" );
        }
        else
        {
            logger.info( "Download skipped" );
        }
    }

    private void removeBrokenDownload( File target, File statusFile )
        throws IOException
    {
        if ( target.exists() && !statusFile.exists() && !maybeDownloading( target ) )
        {
            logger.info( "Removing broken download file {}", target );
            FileUtils.forceDelete( target );
        }
    }

    private boolean maybeDownloading( File target )
    {
        // Check based on assumption that if other thread or jvm is currently downloading file on disk should be modified
        // at least every 10 seconds as new data is being downloaded. This will not work on file system
        // without support for lastmodified field or on very slow internet connection
        return System.currentTimeMillis() - target.lastModified() < TimeUnit.SECONDS.toMillis( 10L );
    }

    private URL getDownloadUrl()
    {
        try
        {
            return new URL( ElasticsearchArtifact.getArtifactUrl() );
        }
        catch ( MalformedURLException e )
        {
            throw new RuntimeException( e );
        }
    }

    private String constructLocalFileName( URL url )
    {
        String path = url.getPath();
        if ( path.isEmpty() )
        {
            return RandomStringUtils.randomAlphanumeric( 10 );
        }
        return FilenameUtils.getName( path );
    }

    private void proceedWithDownload( URL source, File target, File statusFile )
        throws IOException
    {
        logger.info( "Downloading {} to {} ...", source, target );
        copyURLToFile( source, target );
        FileUtils.touch( statusFile );
        logger.info( "Download complete" );
    }

    private void copyURLToFile( URL source, File destination )
        throws IOException
    {
        final URLConnection connection = source.openConnection();
        connection.setConnectTimeout( downloaderConfig.getConnectionTimeoutInMs() );
        connection.setReadTimeout( downloaderConfig.getReadTimeoutInMs() );
        FileUtils.copyInputStreamToFile( connection.getInputStream(), destination );
    }

    private void waitForDownload( File target, File statusFile )
        throws IOException
    {
        boolean downloaded;
        do
        {
            logger.info( "File {} (size={}) is probably being downloaded by another thread/jvm. Waiting ...", target, target.length() );
            downloaded = FileUtils.waitFor( statusFile, 30 );
        }
        while ( !downloaded && maybeDownloading( target ) );
        if ( !downloaded )
        {
            throw new IOException( "Broken download. Another party probably failed to download " + target );
        }
        logger.info( "File was downloaded by another thread/jvm. Download skipped" );
    }

}
