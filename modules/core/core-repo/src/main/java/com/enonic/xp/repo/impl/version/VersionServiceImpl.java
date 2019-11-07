package com.enonic.xp.repo.impl.version;

import java.util.Collection;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.storage.DeleteRequests;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repo.impl.storage.VersionStorageName;
import com.enonic.xp.repo.impl.version.storage.VersionStorageDocFactory;

@Component
public class VersionServiceImpl
    implements VersionService
{
    private static final ReturnFields VERSION_RETURN_FIELDS =
        ReturnFields.from( VersionIndexPath.VERSION_ID, VersionIndexPath.NODE_BLOB_KEY, VersionIndexPath.INDEX_CONFIG_BLOB_KEY,
                           VersionIndexPath.ACCESS_CONTROL_BLOB_KEY, VersionIndexPath.BINARY_BLOB_KEYS, VersionIndexPath.TIMESTAMP,
                           VersionIndexPath.NODE_PATH, VersionIndexPath.NODE_ID, VersionIndexPath.COMMIT_ID, VersionIndexPath.BRANCHES );

    private StorageDao storageDao;

    @Override
    public void deleteBranches( final NodeBranchEntries branchEntries, final InternalContext context, final Boolean all )
    {
        branchEntries.forEach( nodeBranchEntry -> {
            final NodeVersionMetadata nodeVersionMetadata =
                this.getVersion( nodeBranchEntry.getNodeId(), nodeBranchEntry.getVersionId(), context );

            final Branches branches = all
                ? Branches.empty()
                : Branches.from(
                    nodeVersionMetadata.getBranches().stream().filter( branch -> !branch.equals( context.getBranch() ) ).collect(
                        Collectors.toSet() ) );

            this.store( NodeVersionMetadata.
                create( nodeVersionMetadata ).
                addBranches( branches ).
                build(), context );
        } );
    }

    @Override
    public void store( final NodeVersionMetadata nodeVersionMetadata, final InternalContext context )
    {
        final StoreRequest storeRequest = VersionStorageDocFactory.create( nodeVersionMetadata, context.getRepositoryId() );

        this.storageDao.store( storeRequest );
    }

    @Override
    public void delete( final Collection<NodeVersionId> nodeVersionIds, final InternalContext context )
    {
        storageDao.delete( DeleteRequests.create().
            forceRefresh( false ).
            ids( nodeVersionIds.stream().map( NodeVersionId::toString ).collect( Collectors.toList() ) ).
            settings( createVersionStorageSettings( context ) ).
            build() );
    }

    @Override
    public NodeVersionMetadata getVersion( final NodeId nodeId, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        return doGetById( nodeId, nodeVersionId, context );
    }

    private NodeVersionMetadata doGetById( final NodeId nodeId, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create().
            id( nodeVersionId.toString() ).
            returnFields( VERSION_RETURN_FIELDS ).
            storageSettings( createVersionStorageSettings( context ) ).
            routing( nodeId.toString() ).
            build();

        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        return NodeVersionFactory.create( getResult );
    }

    private StorageSource createVersionStorageSettings( final InternalContext context )
    {
        return StorageSource.create().
            storageName( VersionStorageName.from( context.getRepositoryId() ) ).
            storageType( StaticStorageType.VERSION ).
            build();
    }

    @Reference
    public void setStorageDao( final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }
}
