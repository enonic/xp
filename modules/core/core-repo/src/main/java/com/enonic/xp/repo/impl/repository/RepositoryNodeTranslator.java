package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.json.JsonToPropertyTreeTranslator;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repository.IndexConfig;
import com.enonic.xp.repository.IndexConfigs;
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
        toNodeData( repositorySettings.getIndexConfigs(), repositoryData );
        toNodeData( repositorySettings.getValidationSettings(), repositoryData );

        return Node.create().
            id( NodeId.from( repository.getId() ) ).
            childOrder( ChildOrder.defaultOrder() ).
            data( repositoryData ).
            name( repository.getId().toString() ).
            parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH ).
            permissions( AccessControlList.empty() ).
            build();
    }

    private static void toNodeData( final IndexConfigs indexConfigs, final PropertyTree data )
    {
        if ( indexConfigs != null )
        {
            final PropertySet indexConfigsPropertySet = data.addSet( INDEX_CONFIG_KEY );
            final JsonToPropertyTreeTranslator propertyTreeTranslator = new JsonToPropertyTreeTranslator();
            for ( IndexType indexType : IndexType.values() )
            {
                final IndexConfig indexConfig = indexConfigs.get( indexType );
                if ( indexConfig != null )
                {
                    final PropertySet indexConfigPropertySet = indexConfigsPropertySet.addSet( indexType.getName() );
                    final IndexMapping indexMapping = indexConfig.getMapping();
                    if ( indexMapping != null )
                    {
                        final PropertySet indexMappingPropertySet = propertyTreeTranslator.translate( indexMapping.getNode() ).
                            getRoot();
                        indexConfigPropertySet.setSet( MAPPING_KEY, indexMappingPropertySet );
                    }

                    final IndexSettings indexSettings = indexConfig.getSettings();
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

    private static void toNodeData( final ValidationSettings validationSettings, final PropertyTree data )
    {
        if ( validationSettings != null )
        {
            final PropertySet validationSettingsSet = data.addSet( VALIDATION_SETTINGS_KEY );
            validationSettingsSet.setBoolean( CHECK_EXISTS_KEY, validationSettings.isCheckExists() );
            validationSettingsSet.setBoolean( CHECK_PARENT_EXISTS_KEY, validationSettings.isCheckParentExists() );
            validationSettingsSet.setBoolean( CHECK_PERMISSIONS_KEY, validationSettings.isCheckPermissions() );
        }
    }

    public static Repository toRepository( final Node node )
    {
        final PropertyTree nodeData = node.data();

        final RepositorySettings repositorySettings = RepositorySettings.create().
            validationSettings( toValidationSettings( nodeData ) ).
            indexConfigs( toIndexConfigs( nodeData ) ).
            build();

        return Repository.create().
            id( RepositoryId.from( node.id().toString() ) ).
            settings( repositorySettings ).
            build();
    }

    private static ValidationSettings toValidationSettings( final PropertyTree nodeData )
    {
        final PropertySet validationSettingsSet = nodeData.getSet( VALIDATION_SETTINGS_KEY );
        if ( validationSettingsSet != null )
        {
            return ValidationSettings.create().
                checkParentExists( validationSettingsSet.getBoolean( CHECK_PARENT_EXISTS_KEY ) ).
                checkExists( validationSettingsSet.getBoolean( CHECK_EXISTS_KEY ) ).
                checkPermissions( validationSettingsSet.getBoolean( CHECK_PERMISSIONS_KEY ) ).
                build();
        }
        return null;
    }

    private static IndexConfigs toIndexConfigs( final PropertyTree nodeData )
    {
        final PropertySet indexConfigsSet = nodeData.getSet( INDEX_CONFIG_KEY );
        if ( indexConfigsSet != null )
        {
            final IndexConfigs.Builder indexConfigs = IndexConfigs.create();
            for ( IndexType indexType : IndexType.values() )
            {
                final PropertySet indexConfigSet = indexConfigsSet.getSet( indexType.getName() );
                if ( indexConfigSet != null )
                {
                    final IndexConfig.Builder indexConfig = IndexConfig.create();

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
