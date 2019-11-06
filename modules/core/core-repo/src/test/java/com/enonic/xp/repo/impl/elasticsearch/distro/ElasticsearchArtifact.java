package com.enonic.xp.repo.impl.elasticsearch.distro;

import org.apache.commons.lang.SystemUtils;

public enum ElasticsearchArtifact
{
    LINUX( ElasticsearchConstants.DOWNLOAD_FILE_PREFIX + ElasticsearchConstants.VERSION + "-linux-x86_64.tar.gz" ),

    WINDOWS( ElasticsearchConstants.DOWNLOAD_FILE_PREFIX + ElasticsearchConstants.VERSION + "-windows-x86_64.zip" ),

    MAC_OS( ElasticsearchConstants.DOWNLOAD_FILE_PREFIX + ElasticsearchConstants.VERSION + "-darwin-x86_64.tar.gz" );

    private final String archiveName;

    ElasticsearchArtifact( final String archiveName )
    {
        this.archiveName = archiveName;
    }

    public String getArchiveName()
    {
        return archiveName;
    }

    public static String getArchiveNameByOS()
    {
        if ( SystemUtils.IS_OS_WINDOWS )
        {
            return WINDOWS.getArchiveName();
        }
        else if ( SystemUtils.IS_OS_LINUX )
        {
            return LINUX.getArchiveName();
        }
        else if ( SystemUtils.IS_OS_MAC )
        {
            return MAC_OS.getArchiveName();
        }
        else
        {
            throw new IllegalStateException( "Unsupported operation system" );
        }
    }

    public static String getArtifactUrl()
    {
        return ElasticsearchConstants.DOWNLOAD_URL_BASE + getArchiveNameByOS();
    }

}
