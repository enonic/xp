package com.enonic.xp.repo.impl.repository;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.Node;
import com.enonic.xp.repo.impl.index.IndexMapping;
import com.enonic.xp.repo.impl.index.IndexSettings;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RepositoryNodeTranslatorTest
{
    @Test
    public void values_not_duplicated()
        throws Exception
    {
        final PropertyTree indexMapping = new PropertyTree();
        indexMapping.addString( "myMapping", "myMappingValue" );

        final PropertyTree indexSettings = new PropertyTree();
        indexSettings.addString( "mySetting", "mySettingValue" );

        final RepositoryEntry repo = RepositoryEntry.create().
            branches( Branch.from( "master" ) ).
            id( RepositoryId.from( "myrepoid" ) ).
            settings( RepositorySettings.create().
                indexDefinitions( IndexDefinitions.create().
                    add( IndexType.VERSION, IndexDefinition.create().
                        mapping( IndexMapping.from( indexMapping.toMap() ) ).
                        settings( IndexSettings.from( indexSettings.toMap() ) ).
                        build() ).
                    add( IndexType.BRANCH, IndexDefinition.create().
                        mapping( IndexMapping.from( indexMapping.toMap() ) ).
                        settings( IndexSettings.from( indexSettings.toMap() ) ).
                        build() ).
                    add( IndexType.COMMIT, IndexDefinition.create().
                        mapping( IndexMapping.from( indexMapping.toMap() ) ).
                        settings( IndexSettings.from( indexSettings.toMap() ) ).
                        build() ).
                    build() ).
                build() ).
            build();

        final Node node = RepositoryNodeTranslator.toNode( repo );

        final PropertyTree data = node.data();

        assertNotNull( data.getProperty( "indexConfigs.version.mapping.myMapping" ) );
        assertNull( data.getProperty( "indexConfigs.version.mapping.myMapping[1]" ) );
        assertNotNull( data.getProperty( "indexConfigs.version.settings.mySetting" ) );
        assertNull( data.getProperty( "indexConfigs.version.settings.mySetting[1]" ) );

        assertNotNull( data.getProperty( "indexConfigs.branch.mapping.myMapping" ) );
        assertNull( data.getProperty( "indexConfigs.branch.mapping.myMapping[1]" ) );
        assertNotNull( data.getProperty( "indexConfigs.branch.settings.mySetting" ) );
        assertNull( data.getProperty( "indexConfigs.branch.settings.mySetting[1]" ) );
    }
}
