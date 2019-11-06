package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.nio.file.Path;

public interface ElasticsearchConstants
{
    String VERSION = "7.4.2";

    String EXTRACTED_ARCHIVE_NAME = "elasticsearch-" + VERSION;

    String DOWNLOAD_URL_BASE = "https://artifacts.elastic.co/downloads/elasticsearch/";

    Path ES_DIR = Path.of( System.getProperty( "java.io.tmpdir" ), "elasticsearch-dir" );

    String ROOT_DATA_DIR = "elasticsearch-data";

    String TMP_ELASTICSEARCH_DIR = "elasticsearchFixture";

}
