package com.enonic.xp.repo.impl.version;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageData;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repository.RepositoryId;

public class VersionStorageDocFactory
{
    public static StoreRequest create( final NodeVersionMetadata nodeVersion, final RepositoryId repositoryId )
    {
        final StorageData.Builder data = StorageData.create()
            .add( VersionIndexPath.VERSION_ID, nodeVersion.getNodeVersionId().toString() )
            .add( VersionIndexPath.NODE_BLOB_KEY, nodeVersion.getNodeVersionKey().getNodeBlobKey().toString() )
            .add( VersionIndexPath.INDEX_CONFIG_BLOB_KEY, nodeVersion.getNodeVersionKey().getIndexConfigBlobKey().toString() )
            .add( VersionIndexPath.ACCESS_CONTROL_BLOB_KEY, nodeVersion.getNodeVersionKey().getAccessControlBlobKey().toString() )
            .add( VersionIndexPath.BINARY_BLOB_KEYS, nodeVersion.getBinaryBlobKeys() )
            .add( VersionIndexPath.NODE_ID, nodeVersion.getNodeId().toString() )
            .add( VersionIndexPath.TIMESTAMP, nodeVersion.getTimestamp() )
            .add( VersionIndexPath.NODE_PATH, nodeVersion.getNodePath().toString() );

        if ( nodeVersion.getNodeCommitId() != null )
        {
            data.add( VersionIndexPath.COMMIT_ID, nodeVersion.getNodeCommitId().toString() );
        }

        if ( nodeVersion.getAttributes() != null )
        {
            data.add( VersionIndexPath.ATTRIBUTES, attributesToStorage( nodeVersion.getAttributes() ) );
        }

        return StoreRequest.create()
            .nodePath( nodeVersion.getNodePath() )
            .id( nodeVersion.getNodeVersionId().toString() )
            .storage( StorageSource.create()
                          .storageName( StoreStorageName.from( repositoryId ) )
                          .storageType( StaticStorageType.VERSION )
                          .build() )
            .data( data.build() )
            .build();
    }

    private static List<Map<String, Object>> attributesToStorage( final Attributes attributes )
    {
        return attributes.entrySet()
            .stream()
            .map( a -> ImmutableMap.of( "k", a.getKey(), "v", a.getValue().toRawJava() ) )
            .collect( ImmutableList.toImmutableList() );
    }
}
