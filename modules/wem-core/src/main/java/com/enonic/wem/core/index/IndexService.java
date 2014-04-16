package com.enonic.wem.core.index;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.index.NodeIndexDocumentFactory;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.index.elastic.ElasticsearchIndexServiceImpl;
import com.enonic.wem.core.index.elastic.IndexMapping;
import com.enonic.wem.core.index.elastic.IndexMappingProvider;
import com.enonic.wem.core.lifecycle.LifecycleBean;
import com.enonic.wem.core.lifecycle.RunLevel;

@Singleton
public class IndexService
    extends LifecycleBean
{
    private final static Logger LOG = LoggerFactory.getLogger( IndexService.class );

    private final NodeIndexDocumentFactory nodeIndexDocumentFactory = new NodeIndexDocumentFactory();

    private ElasticsearchIndexServiceImpl elasticsearchIndexService;

    private IndexMappingProvider indexMappingProvider;

    private ReindexService reindexService;

    private boolean doReindexOnEmptyIndex = true;

    public IndexService()
    {
        super( RunLevel.L3 );
    }

    @Override
    protected void doStart()
        throws Exception
    {
        doInitializeStoreIndex();
        doInitializeNoDbIndex();
    }

    private void doInitializeNoDbIndex()
        throws Exception
    {
        elasticsearchIndexService.getIndexStatus( Index.NODB, true );

        if ( !indexExists( Index.NODB ) )
        {
            createIndex( Index.NODB );

            if ( doReindexOnEmptyIndex )
            {
                reindexService.reindexContent();
                // TODO: Reindex stuff here
            }
        }
    }

    private void doInitializeStoreIndex()
        throws Exception
    {
        elasticsearchIndexService.getIndexStatus( Index.STORE, true );

        if ( !indexExists( Index.STORE ) )
        {
            createIndex( Index.STORE );

            if ( doReindexOnEmptyIndex )
            {
            }
        }
    }


    @Override
    protected void doStop()
        throws Exception
    {
        // Do nothing
    }

    private boolean indexExists( final Index index )
    {
        return elasticsearchIndexService.indexExists( index );
    }

    public void createIndex( final Index index )
    {
        try
        {
            elasticsearchIndexService.createIndex( index );
        }
        catch ( IndexAlreadyExistsException e )
        {
            LOG.warn( "Tried to create index " + index + ", but index already exists, skipping" );
            return;
        }

        applyMappings( index );
    }

    private void applyMappings( final Index index )
    {
        final List<IndexMapping> allIndexMappings = indexMappingProvider.getMappingsForIndex( index );

        for ( IndexMapping indexMapping : allIndexMappings )
        {
            elasticsearchIndexService.putMapping( indexMapping );
        }
    }

    public void deleteIndex( final Index... indexes )
    {
        for ( final Index index : indexes )
        {
            elasticsearchIndexService.deleteIndex( index );
        }
    }

    public void indexNode( final Node node )
    {
        final Collection<IndexDocument> indexDocuments = nodeIndexDocumentFactory.create( node );
        elasticsearchIndexService.indexDocuments( indexDocuments );
    }

    public void deleteEntity( final EntityId entityId )
    {
        elasticsearchIndexService.delete( new DeleteDocument( Index.NODB, IndexType.NODE, entityId.toString() ) );
    }

    public void setDoReindexOnEmptyIndex( final boolean doReindexOnEmptyIndex )
    {
        this.doReindexOnEmptyIndex = doReindexOnEmptyIndex;
    }

    @Inject
    public void setElasticsearchIndexService( final ElasticsearchIndexServiceImpl elasticsearchIndexService )
    {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }

    @Inject
    public void setIndexMappingProvider( final IndexMappingProvider indexMappingProvider )
    {
        this.indexMappingProvider = indexMappingProvider;
    }

    @Inject
    public void setReindexService( final ReindexService reindexService )
    {
        this.reindexService = reindexService;
    }

    public void reIndex( final Index... indices )
        throws Exception
    {
        for ( final Index index : indices )
        {
            elasticsearchIndexService.getIndexStatus( index, true );

            if ( !indexExists( index ) )
            {
                createIndex( index );
                elasticsearchIndexService.getIndexStatus( index, true );
            }
        }

        this.reindexService.reindexContent();
        this.reindexService.reindexContent();
    }
}
