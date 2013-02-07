package com.enonic.wem.core.search;


import java.util.Collection;
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
import com.enonic.wem.core.search.indexdocument.IndexDocument;


@Component
public class IndexService
{
    private final static Logger LOG = LoggerFactory.getLogger( IndexService.class );

    private IndexDocumentFactory indexDocumentFactory;

    private ElasticsearchIndexServiceImpl elasticsearchIndexService;

    private IndexMappingProvider indexMappingProvider;

    private ReindexService reindexService;

    private boolean doReindexOnEmptyIndex = true;

    @PostConstruct
    public void initialize()
        throws Exception
    {
        IndexStatus indexStatus = elasticsearchIndexService.getIndexStatus( IndexConstants.WEM_INDEX.string(), true );

        LOG.info( "Cluster in state: " + indexStatus.toString() );

        final boolean indexExists = elasticsearchIndexService.indexExists( IndexConstants.WEM_INDEX.string() );

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
            elasticsearchIndexService.createIndex( IndexConstants.WEM_INDEX.string() );
        }
        catch ( IndexAlreadyExistsException e )
        {
            LOG.warn( "Tried to create index, but index already exists, skipping" );
            return;
        }

        final List<IndexMapping> allIndexMappings = indexMappingProvider.getMappingsForIndex( IndexConstants.WEM_INDEX.string() );

        for ( IndexMapping indexMapping : allIndexMappings )
        {
            elasticsearchIndexService.putMapping( indexMapping );
        }
    }

    public void index( final Object indexableData )
    {
        final Collection<IndexDocument> indexDocuments = indexDocumentFactory.create( indexableData );

        elasticsearchIndexService.index( indexDocuments );
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
    public void setReindexService( final ReindexService reindexService )
    {
        this.reindexService = reindexService;
    }

    public void setDoReindexOnEmptyIndex( final boolean doReindexOnEmptyIndex )
    {
        this.doReindexOnEmptyIndex = doReindexOnEmptyIndex;
    }

    @Autowired
    public void setIndexDocumentFactory( final IndexDocumentFactory indexDocumentFactory )
    {
        this.indexDocumentFactory = indexDocumentFactory;
    }
}
