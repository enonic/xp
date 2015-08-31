package com.enonic.wem.repo.internal.storage.branch;

import org.elasticsearch.index.query.QueryBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.BranchDocumentId;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.storage.GetByIdRequest;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageService;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.query.filter.ValueFilter;

@Component
public class BranchServiceImpl
    implements BranchService
{
    private ElasticsearchDao elasticsearchDao;

    private StorageService storageService;

    @Override
    public String store( final StoreBranchDocument storeBranchDocument, final InternalContext context )
    {
        return storageService.store( BranchStorageRequestFactory.create( storeBranchDocument, context ) );
    }

    @Override
    public void delete( final NodeId nodeId, final InternalContext context )
    {
        storageService.delete( BranchDeleteRequestFactory.create( nodeId, context ) );
    }

    @Override
    public NodeBranchVersion get( final NodeId nodeId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create().
            id( new BranchDocumentId( nodeId, context.getBranch() ).toString() ).
            storageSettings( StorageSettings.create().
                storageName( StoreStorageName.from( context.getRepositoryId() ) ).
                storageType( StaticStorageType.BRANCH ).
                build() ).
            returnFields(
                ReturnFields.from( BranchIndexPath.VERSION_ID, BranchIndexPath.STATE, BranchIndexPath.PATH, BranchIndexPath.TIMESTAMP ) ).
            routing( nodeId.toString() ).
            build();

        final GetResult getResult = this.storageService.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        return NodeBranchVersionFactory.create( getResult );
    }

    @Override
    public NodeBranchQueryResult findAll( final NodeBranchQuery nodeBranchQuery, final InternalContext context )
    {
        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( ValueFilter.create().
                fieldName( BranchIndexPath.BRANCH_NAME.getPath() ).
                addValue( ValueFactory.newString( context.getBranch().getName() ) ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveStorageIndexName( context.getRepositoryId() ) ).
            indexType( IndexType.BRANCH.getName() ).
            query( queryBuilder ).
            size( nodeBranchQuery.getSize() ).
            from( nodeBranchQuery.getFrom() ).
            setReturnFields( ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID ) ).
            build();

        final SearchResult searchResult = this.elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            return NodeBranchQueryResult.empty();
        }

        return NodeBranchQueryResultFactory.create( searchResult );
    }

    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }


    @Reference
    public void setStorageService( final StorageService storageService )
    {
        this.storageService = storageService;
    }
}

