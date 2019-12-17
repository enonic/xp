package com.enonic.xp.repo.impl.elasticsearch.distro;


public enum ElasticsearchArtifact
{
    LINUX( ElasticsearchConstants.DOWNLOAD_FILE_PREFIX + ElasticsearchConstants.VERSION + "-no-jdk-linux-x86_64.tar.gz" ),

    WINDOWS( ElasticsearchConstants.DOWNLOAD_FILE_PREFIX + ElasticsearchConstants.VERSION + "-no-jdk-windows-x86_64.zip" ),

    MAC_OS( ElasticsearchConstants.DOWNLOAD_FILE_PREFIX + ElasticsearchConstants.VERSION + "-no-jdk-darwin-x86_64.tar.gz" );

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
        else if ( SystemUtils.IS_OS_MAC )
        {
            return MAC_OS.getArchiveName();
        }
        else
        {
            return LINUX.getArchiveName();
        }
    }

    public static String getArtifactUrl()
    {
        return ElasticsearchConstants.DOWNLOAD_URL_BASE + getArchiveNameByOS();
    }

}
