package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.json.JsonToPropertyTreeTranslator;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repository.IndexDefinition;
import com.enonic.xp.repository.IndexDefinitions;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.repository.ValidationSettings;
import com.enonic.xp.security.acl.AccessControlList;

public class RepositoryNodeTranslator
{
    private static final String INDEX_CONFIG_KEY = "indexConfigs";

    private static final String MAPPING_KEY = "mapping";

    private static final String SETTINGS_KEY = "settings";

    private static final String VALIDATION_SETTINGS_KEY = "validationSettings";

    private static final String CHECK_PARENT_EXISTS_KEY = "checkParentExists";

    private static final String CHECK_EXISTS_KEY = "checkExists";

    private static final String CHECK_PERMISSIONS_KEY = "checkExists";

    public static Node toNode( final Repository repository )
    {
        final PropertyTree repositoryData = new PropertyTree();
        final RepositorySettings repositorySettings = repository.getSettings();
        toNodeData( repositorySettings.getIndexDefinitions(), repositoryData );

        return Node.create().
            id( NodeId.from( repository.getId() ) ).
            childOrder( ChildOrder.defaultOrder() ).
            data( repositoryData ).
            name( repository.getId().toString() ).
            parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH ).
            permissions( AccessControlList.empty() ).
            build();
    }

    private static void toNodeData( final IndexDefinitions indexDefinitions, final PropertyTree data )
    {
        if ( indexDefinitions != null )
        {
            final PropertySet indexConfigsPropertySet = data.addSet( INDEX_CONFIG_KEY );
            final JsonToPropertyTreeTranslator propertyTreeTranslator = new JsonToPropertyTreeTranslator();
            for ( IndexType indexType : IndexType.values() )
            {
                final IndexDefinition indexDefinition = indexDefinitions.get( indexType );
                if ( indexDefinition != null )
                {
                    final PropertySet indexConfigPropertySet = indexConfigsPropertySet.addSet( indexType.getName() );
                    final IndexMapping indexMapping = indexDefinition.getMapping();
                    if ( indexMapping != null )
                    {
                        final PropertySet indexMappingPropertySet = propertyTreeTranslator.translate( indexMapping.getNode() ).
                            getRoot();
                        indexConfigPropertySet.setSet( MAPPING_KEY, indexMappingPropertySet );
                    }

                    final IndexSettings indexSettings = indexDefinition.getSettings();
                    if ( indexSettings != null )
                    {
                        final PropertySet indexSettingsPropertySet = propertyTreeTranslator.translate( indexSettings.getNode() ).
                            getRoot();
                        indexConfigPropertySet.setSet( SETTINGS_KEY, indexSettingsPropertySet );
                    }
                }
            }
        }
    }

    public static Repository toRepository( final Node node )
    {
        final PropertyTree nodeData = node.data();

        final RepositorySettings repositorySettings = RepositorySettings.create().
            indexConfigs( toIndexConfigs( nodeData ) ).
            build();

        return Repository.create().
            id( RepositoryId.from( node.id().toString() ) ).
            settings( repositorySettings ).
            build();
    }

    private static IndexDefinitions toIndexConfigs( final PropertyTree nodeData )
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
                    indexConfig.mapping( mappingSet == null ? null : IndexMapping.from( mappingSet.toTree() ) );

                    final PropertySet settingsSet = indexConfigSet.getSet( SETTINGS_KEY );
                    indexConfig.settings( settingsSet == null ? null : IndexSettings.from( settingsSet.toTree() ) );

                    indexConfigs.add( indexType, indexConfig.build() );
                }
            }
            return indexConfigs.build();
        }
        return null;
    }
}
