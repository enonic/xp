package com.enonic.xp.repo.impl.repository;

import java.util.Objects;
import java.util.Optional;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.index.IndexMapping;
import com.enonic.xp.repo.impl.index.IndexSettings;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.util.Version;

public class RepositoryNodeTranslator
{
    private static final String INDEX_CONFIG_KEY = "indexConfigs";

    private static final String MAPPING_KEY = "mapping";

    private static final String SETTINGS_KEY = "settings";

    private static final String DATA_KEY = "data";

    private static final String TRANSIENT_KEY = "transient";

    private static final String MODEL_VERSION_KEY = "modelVersion";

    public static Node toNode( final RepositoryEntry repository )
    {
        final PropertyTree repositoryNodeData = new PropertyTree();
        final RepositorySettings repositorySettings = repository.getSettings();
        toNodeData( repositorySettings.getIndexDefinitions(), repositoryNodeData );
        toNodeData( repository.getData(), repositoryNodeData );
        if ( !SystemConstants.SYSTEM_REPO_ID.equals( repository.getId() ) && repository.isTransient() )
        {
            repositoryNodeData.setBoolean( TRANSIENT_KEY, true );
        }
        if ( repository.getModelVersion() != null )
        {
            repositoryNodeData.setString( MODEL_VERSION_KEY, repository.getModelVersion().toShortestString() );
        }

        return Node.create()
            .id( NodeId.from( repository.getId() ) )
            .childOrder( ChildOrder.defaultOrder() )
            .data( repositoryNodeData )
            .name( repository.getId().toString() )
            .parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH )
            .permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL )
            .attachedBinaries( repository.getAttachments() )
            .build();
    }

    public static RepositoryEntry toRepository( final Node node )
    {
        final PropertyTree nodeData = node.data();

        final PropertyTree repositoryData = toRepositoryData( nodeData );

        final Version modelVersion =
            Optional.ofNullable( nodeData.getString( MODEL_VERSION_KEY ) ).map( Version::parseVersion ).orElse( null );

        return RepositoryEntry.create()
            .id( RepositoryId.from( node.id().toString() ) )
            .settings( toRepositorySettings( nodeData ) )
            .data( repositoryData )
            .attachments( node.getAttachedBinaries() )
            .transientFlag( Objects.requireNonNullElse( nodeData.getBoolean( TRANSIENT_KEY ), false ) )
            .modelVersion( modelVersion )
            .build();
    }

    public static RepositorySettings toRepositorySettings( final PropertyTree nodeData )
    {
        final PropertySet indexConfigsSet = nodeData.getSet( INDEX_CONFIG_KEY );
        if ( indexConfigsSet != null )
        {
            final IndexDefinitions.Builder indexConfigs = IndexDefinitions.create();
            for ( IndexType indexType : IndexType.values() )
            {
                final PropertySet indexConfigSet = indexConfigsSet.getSet( indexType.getName() );
                if ( indexConfigSet != null )
                {
                    final IndexDefinition.Builder indexConfig = IndexDefinition.create();

                    final PropertySet mappingSet = indexConfigSet.getSet( MAPPING_KEY );
                    indexConfig.mapping( mappingSet == null ? null : IndexMapping.from( mappingSet.toTree().toMap() ) );

                    final PropertySet settingsSet = indexConfigSet.getSet( SETTINGS_KEY );
                    indexConfig.settings( settingsSet == null ? null : IndexSettings.from( settingsSet.toTree().toMap() ) );

                    indexConfigs.add( indexType, indexConfig.build() );
                }
            }
            return RepositorySettings.create().indexDefinitions( indexConfigs.build() ).build();
        }
        return null;
    }

    private static void toNodeData( final PropertyTree repositoryData, final PropertyTree data )
    {
        data.addSet( DATA_KEY, repositoryData.getRoot().copy( data.getRoot().getTree() ) );
    }

    private static void toNodeData( final IndexDefinitions indexDefinitions, final PropertyTree data )
    {
        if ( indexDefinitions != null )
        {
            final PropertySet indexConfigsPropertySet = data.addSet( INDEX_CONFIG_KEY );
            for ( IndexType indexType : IndexType.values() )
            {
                final IndexDefinition indexDefinition = indexDefinitions.get( indexType );
                if ( indexDefinition != null )
                {
                    final PropertySet indexConfigPropertySet = indexConfigsPropertySet.addSet( indexType.getName() );
                    final IndexMapping indexMapping = indexDefinition.getMapping();
                    if ( indexMapping != null )
                    {
                        indexConfigPropertySet.setSet( MAPPING_KEY, PropertyTree.fromMap( indexMapping.getData() ).getRoot().copy( data ) );
                    }

                    final IndexSettings indexSettings = indexDefinition.getSettings();
                    if ( indexSettings != null )
                    {
                        indexConfigPropertySet.setSet( SETTINGS_KEY,
                                                       PropertyTree.fromMap( indexSettings.getData() ).getRoot().copy( data ) );
                    }
                }
            }
        }
    }

    private static PropertyTree toRepositoryData( final PropertyTree nodeData )
    {
        return Optional.ofNullable( nodeData.getSet( DATA_KEY ) ).map( PropertySet::toTree ).orElse( null );
    }
}
