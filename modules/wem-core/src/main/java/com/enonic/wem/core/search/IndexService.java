package com.enonic.wem.core.search;


import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.elastic.ElasticsearchIndexServiceImpl;
import com.enonic.wem.core.search.elastic.IndexMapping;
import com.enonic.wem.core.search.elastic.IndexMappingProvider;

@Component
public class IndexService
    extends IndexConstants
{
    private final static Logger LOG = LoggerFactory.getLogger( IndexService.class );

    private IndexDataFactory indexDataFactory;

    private ElasticsearchIndexServiceImpl elasticsearchIndexService;

    private IndexMappingProvider indexMappingProvider;

    private ReindexService reindexService;

    private boolean doReindexOnEmptyIndex = true;

    @PostConstruct
    public void initialize()
        throws Exception
    {
        IndexStatus indexStatus = elasticsearchIndexService.getIndexStatus( WEM_INDEX, true );

        LOG.info( "Cluster in state: " + indexStatus.toString() );

        final boolean indexExists = elasticsearchIndexService.indexExists( WEM_INDEX );

        if ( !indexExists )
        {
            createIndex();

            if ( doReindexOnEmptyIndex )
            {
                reindexService.reindexAccounts();
            }
        }
    }

    private void createIndex()
    {
        try
        {
            elasticsearchIndexService.createIndex( WEM_INDEX );
        }
        catch ( IndexAlreadyExistsException e )
        {
            LOG.warn( "Tried to create index, but index already exists, skipping" );
            return;
        }

        final List<IndexMapping> allIndexMappings = indexMappingProvider.getMappingsForIndex( WEM_INDEX );

        for ( IndexMapping indexMapping : allIndexMappings )
        {
            elasticsearchIndexService.putMapping( indexMapping );
        }
    }

    public void index( final Object indexableData )
    {
        final IndexData indexData = indexDataFactory.createIndexDataForObject( indexableData );

        if ( indexData != null )
        {
            elasticsearchIndexService.index( indexData );
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

    @Autowired
    public void setIndexDataFactory( final IndexDataFactory indexDataFactory )
    {
        this.indexDataFactory = indexDataFactory;
    }

    @Autowired
    public void setReindexService( final ReindexService reindexService )
    {
        this.reindexService = reindexService;
    }

    public void setDoReindexOnEmptyIndex( final boolean doReindexOnEmptyIndex )
    {
        this.doReindexOnEmptyIndex = doReindexOnEmptyIndex;
    }
}
