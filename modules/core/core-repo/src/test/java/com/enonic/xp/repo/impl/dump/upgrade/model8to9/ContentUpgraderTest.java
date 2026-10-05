package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;

import static org.assertj.core.api.Assertions.assertThat;

class ContentUpgraderTest
{
    private static final RepositoryId DEFAULT_REPO = RepositoryId.from( "com.enonic.cms.default" );

    private final ContentUpgrader upgrader = new ContentUpgrader();

    @Test
    void truncates_date_time_to_millis()
    {
        final PropertyTree data = new PropertyTree();
        data.setInstant( ContentPropertyNames.CREATED_TIME, Instant.parse( "2019-05-06T10:11:12.123456789Z" ) );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, createContentNode( data ) );

        assertThat( result ).isNotNull();
        assertThat( result.data().getInstant( ContentPropertyNames.CREATED_TIME ) ).isEqualTo( Instant.parse( "2019-05-06T10:11:12.123Z" ) );
    }

    @Test
    void skips_date_time_with_millis_precision()
    {
        final PropertyTree data = new PropertyTree();
        data.setInstant( ContentPropertyNames.CREATED_TIME, Instant.parse( "2019-05-06T10:11:12.123Z" ) );

        assertThat( upgrader.upgradeNodeVersion( DEFAULT_REPO, createContentNode( data ) ) ).isNull();
    }

    @Test
    void converts_string_time_properties_to_date_time()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.CREATED_TIME, "2019-05-06T10:11:12.123456Z" );
        data.setString( ContentPropertyNames.MODIFIED_TIME, "2019-05-06T10:11:12Z" );
        final PropertySet publish = data.addSet( ContentPropertyNames.PUBLISH_INFO );
        publish.setString( ContentPropertyNames.PUBLISH_FROM, "2019-05-06T10:11:12.123456Z" );
        publish.setString( ContentPropertyNames.PUBLISH_FIRST, "2019-05-06T10:11:12.123Z" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, createContentNode( data ) );

        assertThat( result ).isNotNull();
        final PropertyTree upgraded = result.data();
        assertThat( upgraded.getProperty( ContentPropertyNames.CREATED_TIME ).getType() ).isEqualTo( ValueTypes.DATE_TIME );
        assertThat( upgraded.getInstant( ContentPropertyNames.CREATED_TIME ) ).isEqualTo( Instant.parse( "2019-05-06T10:11:12.123Z" ) );
        assertThat( upgraded.getProperty( ContentPropertyNames.MODIFIED_TIME ).getType() ).isEqualTo( ValueTypes.DATE_TIME );
        assertThat( upgraded.getInstant( ContentPropertyNames.MODIFIED_TIME ) ).isEqualTo( Instant.parse( "2019-05-06T10:11:12Z" ) );

        final PropertySet upgradedPublish = upgraded.getSet( ContentPropertyNames.PUBLISH_INFO );
        assertThat( upgradedPublish.getProperty( ContentPropertyNames.PUBLISH_FROM ).getType() ).isEqualTo( ValueTypes.DATE_TIME );
        assertThat( upgradedPublish.getInstant( ContentPropertyNames.PUBLISH_FROM ) ).isEqualTo(
            Instant.parse( "2019-05-06T10:11:12.123Z" ) );
        assertThat( upgradedPublish.getProperty( ContentPropertyNames.PUBLISH_FIRST ).getType() ).isEqualTo( ValueTypes.DATE_TIME );
    }

    @Test
    void leaves_unparseable_string_as_is()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.CREATED_TIME, "not a date" );

        assertThat( upgrader.upgradeNodeVersion( DEFAULT_REPO, createContentNode( data ) ) ).isNull();
        assertThat( data.getString( ContentPropertyNames.CREATED_TIME ) ).isEqualTo( "not a date" );
    }

    @Test
    void skips_non_project_repo()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.CREATED_TIME, "2019-05-06T10:11:12.123456Z" );

        assertThat( upgrader.upgradeNodeVersion( RepositoryId.from( "system-repo" ), createContentNode( data ) ) ).isNull();
    }

    private static NodeStoreVersion createContentNode( final PropertyTree data )
    {
        return NodeStoreVersion.create()
            .id( NodeId.from( "test-node" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .build();
    }
}
