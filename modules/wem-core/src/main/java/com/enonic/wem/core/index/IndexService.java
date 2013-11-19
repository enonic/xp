package com.enonic.wem.core.index;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.index.NodeIndexDocumentFactory;
import com.enonic.wem.core.index.account.AccountDeleteDocumentFactory;
import com.enonic.wem.core.index.account.AccountIndexDocumentFactory;
import com.enonic.wem.core.index.content.ContentDeleteDocumentFactory;
import com.enonic.wem.core.index.content.ContentIndexDocumentsFactory;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.index.document.IndexDocument2;
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

    private ElasticsearchIndexServiceImpl elasticsearchIndexService;

    private IndexMappingProvider indexMappingProvider;

    private ReindexService reindexService;

    private boolean doReindexOnEmptyIndex = true;

    public IndexService()
    {
        super( RunLevel.L2 );
    }

    @Override
    protected void doStart()
        throws Exception
    {
        doInitializeOldWemIndex();
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
                // TODO: Reindex stuff here
            }
        }
    }

    private void doInitializeOldWemIndex()
        throws Exception
    {
        elasticsearchIndexService.getIndexStatus( Index.WEM, true );

        if ( !indexExists( Index.WEM ) )
        {
            createIndex( Index.WEM );

            if ( doReindexOnEmptyIndex )
            {
                reindexService.reindexContent();
                reindexService.reindexAccounts();
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

    private void createIndex( final Index index )
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

    private void deleteIndex( final Index index )
    {
        elasticsearchIndexService.deleteIndex( index );
    }

    public void indexAccount( final Account account )
    {
        final Collection<IndexDocument> indexDocuments = AccountIndexDocumentFactory.create( account );

        elasticsearchIndexService.index( indexDocuments );
    }

    public void indexContent( final Content content )
    {
        if ( !content.isTemporary() )
        {
            final Collection<IndexDocument> indexDocuments = ContentIndexDocumentsFactory.create( content );
            elasticsearchIndexService.index( indexDocuments );
        }
    }

    public void indexNode( final Node node )
    {
        NodeIndexDocumentFactory nodeIndexDocumentFactory = new NodeIndexDocumentFactory();

        final Collection<IndexDocument2> indexDocuments = nodeIndexDocumentFactory.create( node );
        elasticsearchIndexService.indexDocuments( indexDocuments );
    }

    public void deleteEntity( final EntityId entityId )
    {
        elasticsearchIndexService.delete( new DeleteDocument( Index.NODB, IndexType.NODE, entityId.toString() ) );
    }

    public void deleteAccount( final AccountKey accountKey )
    {
        final DeleteDocument deleteDocument = AccountDeleteDocumentFactory.create( accountKey );

        this.elasticsearchIndexService.delete( deleteDocument );
    }

    public void deleteContent( final ContentId contentId )
    {
        final Collection<DeleteDocument> deleteDocuments = ContentDeleteDocumentFactory.create( contentId );

        for ( DeleteDocument deleteDocument : deleteDocuments )
        {
            this.elasticsearchIndexService.delete( deleteDocument );
        }
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
            if ( indexExists( index ) )
            {
                deleteIndex( index );
                elasticsearchIndexService.getIndexStatus( index, true );
                createIndex( index );
                elasticsearchIndexService.getIndexStatus( index, true );
            }
        }

        this.reindexService.reindexContent();
        this.reindexService.reindexAccounts();
    }
}
