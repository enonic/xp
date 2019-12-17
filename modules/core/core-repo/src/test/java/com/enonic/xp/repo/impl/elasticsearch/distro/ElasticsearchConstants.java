package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.nio.file.Path;

public interface ElasticsearchConstants
{
    String DOWNLOAD_FILE_PREFIX = "elasticsearch-";

    String VERSION = "7.5.0";

    String EXTRACTED_ARCHIVE_NAME = "elasticsearch-" + VERSION;

    String DOWNLOAD_URL_BASE = "https://artifacts.elastic.co/downloads/elasticsearch/";

    Path ES_DIR = Path.of( System.getProperty( "java.io.tmpdir" ), "elasticsearch-dir" );

    Path ES_EXECUTABLE_PATH = ES_DIR.resolve( EXTRACTED_ARCHIVE_NAME ).resolve( "bin" ).resolve( getExecutableName() );

    Path ES_CONFIG_EXTRACTED_PATH = ES_DIR.resolve( EXTRACTED_ARCHIVE_NAME ).resolve( "config" );

    String ROOT_DATA_DIR_NAME = "elasticsearch-data";

    String ELASTICSEARCH_TMP_DIR_NAME = "temp";

    static String getExecutableName()
    {
        if ( SystemUtils.IS_OS_WINDOWS )
        {
            return "elasticsearch.bat";
        }
        else
        {
            return "elasticsearch";
        }
    }
}
