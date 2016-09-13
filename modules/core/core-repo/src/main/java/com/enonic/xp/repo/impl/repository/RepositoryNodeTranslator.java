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
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.security.acl.AccessControlList;

public class RepositoryNodeTranslator
{
    public static Node toNode( final RepositorySettings repositorySettings )
    {
        final PropertyTree repositoryData = new PropertyTree();

        final IndexConfigs indexConfigs = repositorySettings.getIndexConfigs();
        if ( indexConfigs != null )
        {
            final PropertySet indexConfigsPropertySet = repositoryData.addSet( "indexConfigs" );
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
                        indexConfigPropertySet.setSet( "mapping", indexMappingPropertySet );
                    }

                    final IndexSettings indexSettings = indexConfig.getSettings();
                    if ( indexSettings != null )
                    {
                        final PropertySet indexSettingsPropertySet = propertyTreeTranslator.translate( indexSettings.getNode() ).
                            getRoot();
                        indexConfigPropertySet.setSet( "settings", indexSettingsPropertySet );
                    }
                }
            }
        }

        return Node.create().
            id( NodeId.from( repositorySettings.getRepositoryId() ) ).
            childOrder( ChildOrder.defaultOrder() ).
            data( repositoryData ).
            name( repositorySettings.getRepositoryId().toString() ).
            parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH ).
            permissions( AccessControlList.empty() ).
            build();
    }

    public static Repository fromNode( final Node node )
    {
        return Repository.create().
            id( RepositoryId.from( node.id().toString() ) ).
            build();
    }
}
