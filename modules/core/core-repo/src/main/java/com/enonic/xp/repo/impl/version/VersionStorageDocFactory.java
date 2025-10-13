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
            .add( VersionIndexPath.VERSION_ID.getPath(), nodeVersion.getNodeVersionId().toString() )
            .add( VersionIndexPath.NODE_BLOB_KEY.getPath(), nodeVersion.getNodeVersionKey().getNodeBlobKey().toString() )
            .add( VersionIndexPath.INDEX_CONFIG_BLOB_KEY.getPath(), nodeVersion.getNodeVersionKey().getIndexConfigBlobKey().toString() )
            .add( VersionIndexPath.ACCESS_CONTROL_BLOB_KEY.getPath(), nodeVersion.getNodeVersionKey().getAccessControlBlobKey().toString() )
            .add( VersionIndexPath.BINARY_BLOB_KEYS.getPath(), nodeVersion.getBinaryBlobKeys() )
            .add( VersionIndexPath.NODE_ID.getPath(), nodeVersion.getNodeId().toString() )
            .add( VersionIndexPath.TIMESTAMP.getPath(), nodeVersion.getTimestamp() )
            .add( VersionIndexPath.NODE_PATH.getPath(), nodeVersion.getNodePath().toString() );

        if ( nodeVersion.getNodeCommitId() != null )
        {
            data.add( VersionIndexPath.COMMIT_ID.getPath(), nodeVersion.getNodeCommitId().toString() );
        }

        if ( nodeVersion.getAttributes() != null )
        {
            data.add( VersionIndexPath.ATTRIBUTES.getPath(), attributesToString( nodeVersion.getAttributes() ) );
        }

        return StoreRequest.create()
            .nodePath( nodeVersion.getNodePath() )
            .id( nodeVersion.getNodeVersionId().toString() )
            .settings( StorageSource.create()
                           .storageName( StoreStorageName.from( repositoryId ) )
                           .storageType( StaticStorageType.VERSION )
                           .build() )
            .data( data.build() )
            .build();
    }

    private static List<Map<String, Object>> attributesToString( final Attributes attributes )
    {
        return attributes.list()
            .stream()
            .map( a -> ImmutableMap.of( "k", a.getKey(), "v", a.getValue().rawJava() ) )
            .collect( ImmutableList.toImmutableList() );
    }
}
