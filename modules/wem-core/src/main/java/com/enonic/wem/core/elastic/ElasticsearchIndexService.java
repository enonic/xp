package com.enonic.wem.core.elastic;

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
import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.lifecycle.LifecycleBean;
import com.enonic.wem.core.lifecycle.RunLevel;

@Singleton
public class ElasticsearchIndexService
    extends LifecycleBean
    implements IndexService
{
    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexService.class );

    private final NodeIndexDocumentFactory nodeIndexDocumentFactory = new NodeIndexDocumentFactory();

    private ElasticsearchService elasticsearchService;

    private IndexMappingProvider indexMappingProvider;

    private boolean doReindexOnEmptyIndex = true;

    public ElasticsearchIndexService()
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
        elasticsearchService.getIndexStatus( Index.NODB, true );

        if ( !indexExists( Index.NODB ) )
        {
            createIndex( Index.NODB );

            if ( doReindexOnEmptyIndex )
            {
                //        reindexService.reindexContent();
                // TODO: Reindex stuff here
            }
        }
    }

    private void doInitializeStoreIndex()
        throws Exception
    {
        elasticsearchService.getIndexStatus( Index.STORE, true );

        if ( !indexExists( Index.STORE ) )
        {
            createIndex( Index.STORE );
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
        return elasticsearchService.indexExists( index );
    }

    public void createIndex( final Index index )
    {
        try
        {
            elasticsearchService.createIndex( index );
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
            elasticsearchService.putMapping( indexMapping );
        }
    }

    public void deleteIndex( final Index... indexes )
    {
        for ( final Index index : indexes )
        {
            elasticsearchService.deleteIndex( index );
        }
    }

    public void indexNode( final Node node )
    {
        final Collection<IndexDocument> indexDocuments = nodeIndexDocumentFactory.create( node );
        elasticsearchService.indexDocuments( indexDocuments );
    }

    public void deleteEntity( final EntityId entityId )
    {
        elasticsearchService.delete( new DeleteDocument( Index.NODB, IndexType.NODE, entityId.toString() ) );
    }

    public void setDoReindexOnEmptyIndex( final boolean doReindexOnEmptyIndex )
    {
        this.doReindexOnEmptyIndex = doReindexOnEmptyIndex;
    }

    @Inject
    public void setElasticsearchService( final ElasticsearchService elasticsearchService )
    {
        this.elasticsearchService = elasticsearchService;
    }

    @Inject
    public void setIndexMappingProvider( final IndexMappingProvider indexMappingProvider )
    {
        this.indexMappingProvider = indexMappingProvider;
    }
}
