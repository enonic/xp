package com.enonic.wem.core.search;


import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.elastic.ElasticsearchIndexServiceImpl;
import com.enonic.wem.core.search.elastic.IndexMapping;
import com.enonic.wem.core.search.elastic.IndexMappingProvider;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Component
public class IndexServiceImpl
{
    private final static Logger LOG = LoggerFactory.getLogger( IndexServiceImpl.class );

    private final static String INDEX_NAME = "cms";

    private ElasticsearchIndexServiceImpl elasticsearchIndexService;

    private IndexMappingProvider indexMappingProvider;

    @PostConstruct
    public void initialize()
        throws Exception
    {
        IndexStatus indexStatus = elasticsearchIndexService.getIndexStatus( INDEX_NAME, true );

        LOG.info( "Cluster in state: " + indexStatus.toString() );

        final boolean indexExists = elasticsearchIndexService.indexExists( INDEX_NAME );

        if ( !indexExists )
        {
            createIndex();
        }
    }

    private void createIndex()
    {
        try
        {
            elasticsearchIndexService.createIndex( INDEX_NAME );
        }
        catch ( IndexAlreadyExistsException e )
        {
            LOG.warn( "Tried to create index, but index already exists, skipping" );
            return;
        }

        final List<IndexMapping> allIndexMappings = indexMappingProvider.getMappingsForIndex( INDEX_NAME );

        for ( IndexMapping indexMapping : allIndexMappings )
        {
            elasticsearchIndexService.putMapping( indexMapping );
        }
    }

    @Autowired
    public void setElasticsearchIndexService( final ElasticsearchIndexServiceImpl elasticsearchIndexService )
    {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }

    @Autowired
    public void setIndexMappingProvider( final IndexMappingProvider indexMappingProvider )
    {
        this.indexMappingProvider = indexMappingProvider;
    }

    public static void main( String... args )
        throws Exception
    {
        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder().loadFromSource(
            jsonBuilder().startObject().startObject( "analysis" ).startObject( "analyzer" ).startObject( "keywordlowercase" ).field( "type",
                                                                                                                                     "custom" ).field(
                "tokenizer", "keyword" ).field( "filter",
                                                new String[]{"lowercase"} ).endObject().endObject().endObject().endObject().string() );

        final Settings build = settings.build();


    }

}
