package com.enonic.xp.repo.impl.repository;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.Node;
import com.enonic.xp.repository.IndexDefinition;
import com.enonic.xp.repository.IndexDefinitions;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySettings;

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

        final Repository repo = Repository.create().
            branches( Branch.from( "master" ) ).
            id( RepositoryId.from( "myrepoid" ) ).
            settings( RepositorySettings.create().
                indexDefinitions( IndexDefinitions.create().
                    add( IndexType.STORAGE, IndexDefinition.create().
                        mapping( IndexMapping.from( indexMapping ) ).
                        settings( IndexSettings.from( indexSettings ) ).
                        build() ).
                    add( IndexType.COMMIT, IndexDefinition.create().
                        mapping( IndexMapping.from( indexMapping ) ).
                        settings( IndexSettings.from( indexSettings ) ).
                        build() ).
                    build() ).
                build() ).
            build();

        final Node node = RepositoryNodeTranslator.toNode( repo );

        final PropertyTree data = node.data();

        assertNotNull( data.getProperty( "indexConfigs.storage.mapping.myMapping" ) );
        assertNull( data.getProperty( "indexConfigs.storage.mapping.myMapping[1]" ) );
        assertNotNull( data.getProperty( "indexConfigs.storage.settings.mySetting" ) );
        assertNull( data.getProperty( "indexConfigs.storage.settings.mySetting[1]" ) );

        assertNotNull( data.getProperty( "indexConfigs.commit.mapping.myMapping" ) );
        assertNull( data.getProperty( "indexConfigs.commit.mapping.myMapping[1]" ) );
        assertNotNull( data.getProperty( "indexConfigs.commit.settings.mySetting" ) );
        assertNull( data.getProperty( "indexConfigs.commit.settings.mySetting[1]" ) );
    }
}
