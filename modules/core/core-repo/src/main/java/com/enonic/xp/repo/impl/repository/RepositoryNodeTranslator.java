package com.enonic.xp.repo.impl.repository;

import java.util.LinkedList;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.BranchInfo;
import com.enonic.xp.branch.BranchInfos;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repository.IndexDefinition;
import com.enonic.xp.repository.IndexDefinitions;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.security.SystemConstants;

public class RepositoryNodeTranslator
{
    private static final String BRANCH_INFOS_KEY = "branches";

    private static final String BRANCH_NAME_KEY = "name";

    private static final String PARENT_BRANCH_NAME_KEY = "parent";

    private static final String INDEX_CONFIG_KEY = "indexConfigs";

    private static final String MAPPING_KEY = "mapping";

    private static final String SETTINGS_KEY = "settings";

    public static Node toNode( final Repository repository )
    {
        final PropertyTree repositoryData = new PropertyTree();
        toNodeData( repository.getBranchInfos(), repositoryData );
        final RepositorySettings repositorySettings = repository.getSettings();
        toNodeData( repositorySettings.getIndexDefinitions(), repositoryData );

        return Node.create().
            id( NodeId.from( repository.getId() ) ).
            childOrder( ChildOrder.defaultOrder() ).
            data( repositoryData ).
            name( repository.getId().toString() ).
            parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH ).
            permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
            build();
    }

    public static NodeEditor toCreateBranchNodeEditor( final BranchInfo branchInfo )
    {
        return toBeEdited -> toNodeData( branchInfo, toBeEdited.data );
    }

    public static NodeEditor toDeleteBranchNodeEditor( final Branch branchToDelete )
    {
        return toBeEdited -> {
            final BranchInfos existingBranchInfos = toBranchInfos( toBeEdited.data );

            toBeEdited.data.removeProperties( BRANCH_INFOS_KEY );
            for ( BranchInfo existingBranchInfo : existingBranchInfos )
            {
                if ( !branchToDelete.getValue().equals( existingBranchInfo.getBranch().getValue() ) )
                {
                    toNodeData(existingBranchInfo, toBeEdited.data);
                }
            }
        };
    }

    private static void toNodeData( final BranchInfos branchInfos, final PropertyTree data )
    {
        branchInfos.forEach( branchInfo -> toNodeData( branchInfo, data ) );
    }

    private static void toNodeData( final BranchInfo branchInfo, final PropertyTree data )
    {
        final PropertySet branchInfoSet = data.addSet( BRANCH_INFOS_KEY );
        branchInfoSet.setString( BRANCH_NAME_KEY, branchInfo.getBranch().getValue() );
        if ( branchInfo.getParentBranch() != null )
        {
            branchInfoSet.setString( PARENT_BRANCH_NAME_KEY, branchInfo.getParentBranch().getValue() );
        }
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
                        final PropertySet indexMappingPropertySet = JsonToPropertyTreeTranslator.translate( indexMapping.getNode() ).
                            getRoot();
                        indexConfigPropertySet.setSet( MAPPING_KEY, indexMappingPropertySet );
                    }

                    final IndexSettings indexSettings = indexDefinition.getSettings();
                    if ( indexSettings != null )
                    {
                        final PropertySet indexSettingsPropertySet = JsonToPropertyTreeTranslator.translate( indexSettings.getNode() ).
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
            indexDefinitions( toIndexConfigs( nodeData ) ).
            build();

        return Repository.create().
            id( RepositoryId.from( node.id().toString() ) ).
            branchInfos( toBranchInfos( nodeData ) ).
            settings( repositorySettings ).
            build();
    }

    private static BranchInfos toBranchInfos( final PropertyTree nodeData )
    {
        final LinkedList branchInfos = new LinkedList();
        for ( PropertySet branchInfoSet: nodeData.getSets( BRANCH_INFOS_KEY ))
        {
            final String branch = branchInfoSet.getString( BRANCH_NAME_KEY );
            final String parentBranch = branchInfoSet.getString( PARENT_BRANCH_NAME_KEY );
            final BranchInfo branchInfo = BranchInfo.create().
                branch( branch ).
                parentBranch( parentBranch ).
                build();
            branchInfos.add( branchInfo );
        }
        return BranchInfos.from( branchInfos );
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
