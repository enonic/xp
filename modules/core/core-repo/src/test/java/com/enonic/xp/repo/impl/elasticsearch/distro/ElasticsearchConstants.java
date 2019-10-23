package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.File;

public interface ElasticsearchConstants
{
    String EXTRACTED_ARCHIVE_NAME = "elasticsearch-7.4.0";

    File ES_DIR = new File( System.getProperty( "java.io.tmpdir" ), "elasticsearch-dir" );

    String ROOT_DATA_DIR = "elasticsearch-data";

    String TMP_ELASTICSEARCH_DIR = "elasticsearchFixture";

}
