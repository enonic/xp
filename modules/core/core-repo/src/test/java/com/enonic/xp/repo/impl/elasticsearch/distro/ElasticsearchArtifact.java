package com.enonic.xp.repo.impl.elasticsearch.distro;

import org.apache.commons.lang.SystemUtils;

public enum ElasticsearchArtifact
{
    LINUX( "elasticsearch-oss-7.4.2-linux-x86_64.tar.gz" ),

    WINDOWS( "elasticsearch-oss-7.4.2-windows-x86_64.zip" ),

    MAC_OS( "elasticsearch-oss-7.4.2-darwin-x86_64.tar.gz" );

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
        return "https://artifacts.elastic.co/downloads/elasticsearch/" + getArchiveNameByOS();
    }

}
