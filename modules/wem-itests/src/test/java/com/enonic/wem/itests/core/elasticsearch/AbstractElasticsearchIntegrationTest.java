package com.enonic.wem.itests.core.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.repository.IndexNameResolver;
import com.enonic.wem.core.repository.RepositoryIndexMappingProvider;

public abstract class AbstractElasticsearchIntegrationTest
    extends ElasticsearchIntegrationTest
{
    protected ElasticsearchDao elasticsearchDao;

    private ElasticsearchIndexService elasticsearchIndexService;

    protected Client client;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.client = client();
        this.elasticsearchDao = new ElasticsearchDao();
        this.elasticsearchDao.setClient( client );

        this.elasticsearchIndexService = new ElasticsearchIndexService();
        elasticsearchIndexService.setElasticsearchDao( elasticsearchDao );
        elasticsearchIndexService.setClient( client );
    }


    protected void createSearchIndex( final Repository repository )
    {
        final String indexName = IndexNameResolver.resolveSearchIndexName( repository.getId() );
        this.elasticsearchIndexService.createIndex( indexName, getContentRepoSearchDefaultSettings() );
        this.elasticsearchIndexService.applyMapping( IndexNameResolver.resolveSearchIndexName( repository.getId() ),
                                                     IndexType._DEFAULT_.getName(),
                                                     RepositoryIndexMappingProvider.getSearchMappings( repository ) );

        assertTrue( indexExists( indexName ) );
    }


    String getContentRepoSearchDefaultSettings()
    {
        return RepositoryTestSearchIndexSettingsProvider.getSettings( ContentConstants.CONTENT_REPO );
    }

    @Override
    protected Settings nodeSettings( int nodeOrdinal )
    {
        return ImmutableSettings.settingsBuilder().
            put( "store.type", "memory" ).
            put( "path.data", "target/tmp/es-data" ).
            put( super.nodeSettings( nodeOrdinal ) ).
            build();
    }

}
