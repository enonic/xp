package com.enonic.xp.repo.impl.repository;

import java.util.Iterator;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.branch.Branch;
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
import com.enonic.xp.repository.RepositoryData;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.security.SystemConstants;

public class RepositoryNodeTranslator
{
    private static final String BRANCHES_KEY = "branches";

    private static final String INDEX_CONFIG_KEY = "indexConfigs";

    private static final String MAPPING_KEY = "mapping";

    private static final String SETTINGS_KEY = "settings";

    private static final String DATA_KEY = "data";

    public static Node toNode( final Repository repository )
    {
        final PropertyTree repositoryNodeData = new PropertyTree();
        toNodeData( repository.getBranches(), repositoryNodeData );
        final RepositorySettings repositorySettings = repository.getSettings();
        toNodeData( repositorySettings.getIndexDefinitions(), repositoryNodeData );
        toNodeData( repository.getData(), repositoryNodeData );

        return Node.create().
            id( NodeId.from( repository.getId() ) ).
            childOrder( ChildOrder.defaultOrder() ).
            data( repositoryNodeData ).
            name( repository.getId().toString() ).
            parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH ).
            permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
            build();
    }

    public static NodeEditor toCreateBranchNodeEditor( final Branch branch )
    {
        return toBeEdited -> toBeEdited.data.addString( BRANCHES_KEY, branch.getValue() );
    }

    public static NodeEditor toUpdateDataNodeEditor( RepositoryData data )
    {
        return toBeEdited -> toBeEdited.data.setSet( DATA_KEY, data.getValue().getRoot() );
    }

    public static NodeEditor toDeleteBranchNodeEditor( final Branch branch )
    {
        return toBeEdited -> {
            final Iterable<String> branches = toBeEdited.data.getStrings( BRANCHES_KEY );

            toBeEdited.data.removeProperties( BRANCHES_KEY );
            for ( Iterator<String> branchIterator = branches.iterator(); branchIterator.hasNext(); )
            {
                final String currentBranch = branchIterator.next();
                if ( !branch.getValue().equals( currentBranch ) )
                {
                    toBeEdited.data.addString( BRANCHES_KEY, currentBranch );
                }
            }
        };
    }

    private static void toNodeData( final Branches branches, final PropertyTree data )
    {
        branches.forEach( branch -> data.addString( BRANCHES_KEY, branch.getValue() ) );
    }

    private static void toNodeData( final RepositoryData repositoryData, final PropertyTree data )
    {
        data.addSet( DATA_KEY, repositoryData.getValue().getRoot().detach() );
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

        final RepositoryData repositoryData = toRepositoryData( nodeData );

        return Repository.create().
            id( RepositoryId.from( node.id().toString() ) ).
            branches( toBranches( nodeData ) ).
            settings( repositorySettings ).
            data( repositoryData ).
            build();
    }

    private static Branches toBranches( final PropertyTree nodeData )
    {
        final ImmutableSet.Builder<Branch> branches = ImmutableSet.builder();
        nodeData.getStrings( BRANCHES_KEY ).
            forEach( branchValue -> branches.add( Branch.from( branchValue ) ) );
        return Branches.from( branches.build() );
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

    private static RepositoryData toRepositoryData( final PropertyTree nodeData )
    {
        PropertySet dataSet = nodeData.getSet( DATA_KEY );
        if ( dataSet != null )
        {
            return RepositoryData.from( dataSet.toTree() );
        }
        return null;
    }
}
