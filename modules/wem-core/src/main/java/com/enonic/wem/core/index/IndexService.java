package com.enonic.wem.core.index;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.core.index.account.AccountDeleteDocumentFactory;
import com.enonic.wem.core.index.account.AccountIndexDocumentFactory;
import com.enonic.wem.core.index.content.ContentDeleteDocumentFactory;
import com.enonic.wem.core.index.content.ContentIndexDocumentsFactory;
import com.enonic.wem.core.index.elastic.ElasticsearchIndexServiceImpl;
import com.enonic.wem.core.index.elastic.IndexMapping;
import com.enonic.wem.core.index.elastic.IndexMappingProvider;
import com.enonic.wem.core.index.indexdocument.IndexDocument;

@Component
public class IndexService
{
    private final static Logger LOG = LoggerFactory.getLogger( IndexService.class );

    private ElasticsearchIndexServiceImpl elasticsearchIndexService;

    private IndexMappingProvider indexMappingProvider;

    private ReindexService reindexService;

    private boolean doReindexOnEmptyIndex = true;

    @PostConstruct
    public void initialize()
        throws Exception
    {
        elasticsearchIndexService.getIndexStatus( IndexConstants.WEM_INDEX, true );

        final boolean indexExists = elasticsearchIndexService.indexExists( IndexConstants.WEM_INDEX );

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
            elasticsearchIndexService.createIndex( IndexConstants.WEM_INDEX );
        }
        catch ( IndexAlreadyExistsException e )
        {
            LOG.warn( "Tried to create index, but index already exists, skipping" );
            return;
        }

        final List<IndexMapping> allIndexMappings = indexMappingProvider.getMappingsForIndex( IndexConstants.WEM_INDEX );

        for ( IndexMapping indexMapping : allIndexMappings )
        {
            elasticsearchIndexService.putMapping( indexMapping );
        }
    }

    public void indexAccount( final Account account )
    {
        final Collection<IndexDocument> indexDocuments = AccountIndexDocumentFactory.create( account );

        elasticsearchIndexService.index( indexDocuments );
    }

    public void indexContent( final Content content )
    {
        final Collection<IndexDocument> indexDocuments = ContentIndexDocumentsFactory.create( content );

        elasticsearchIndexService.index( indexDocuments );
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

}
