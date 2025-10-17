package com.enonic.xp.repo.impl.version;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageData;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.Attributes;
import com.enonic.xp.util.PropertyValue;

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

    private static List<String> attributesToString( final Attributes attributes )
    {
        SimpleModule module = new SimpleModule();
        module.addSerializer( PropertyValue.class, new PropertyValueSerializer() );

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule( module );

        var result = new ArrayList<String>();
        try
        {
            for ( PropertyValue p : attributes.list() )
            {
                result.add( mapper.writeValueAsString( p ) );
            }
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( e );
        }
        return result;
    }

}
