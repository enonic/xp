package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;

import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.SystemIdProviderConfigUpgrader.APPLICATION_KEY_PROPERTY;
import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.SystemIdProviderConfigUpgrader.CONFIG_PROPERTY;
import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.SystemIdProviderConfigUpgrader.STANDARD_ID_PROVIDER_APPLICATION;
import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.SystemIdProviderConfigUpgrader.SYSTEM_ID_PROVIDER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

class SystemIdProviderConfigUpgraderTest
{
    private final SystemIdProviderConfigUpgrader upgrader = new SystemIdProviderConfigUpgrader();

    @Test
    void adds_config_when_missing()
    {
        final NodeStoreVersion nodeVersion = createVersion( new PropertyTree() );

        final NodeStoreVersion result = upgrader.upgrade( SystemConstants.SYSTEM_REPO_ID, SYSTEM_ID_PROVIDER_PATH, nodeVersion );

        assertThat( result ).isSameAs( nodeVersion );
        assertThat( result.data().getString( APPLICATION_KEY_PROPERTY ) ).isEqualTo( STANDARD_ID_PROVIDER_APPLICATION );
        assertThat( result.data().getSet( CONFIG_PROPERTY ) ).isNotNull();
    }

    @Test
    void keeps_existing_config_set_when_application_missing()
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet config = data.newSet();
        config.setString( "title", "Login" );
        data.setSet( CONFIG_PROPERTY, config );

        final NodeStoreVersion result = upgrader.upgrade( SystemConstants.SYSTEM_REPO_ID, SYSTEM_ID_PROVIDER_PATH, createVersion( data ) );

        assertThat( result ).isNotNull();
        assertThat( result.data().getString( APPLICATION_KEY_PROPERTY ) ).isEqualTo( STANDARD_ID_PROVIDER_APPLICATION );
        assertThat( result.data().getSet( CONFIG_PROPERTY ).getString( "title" ) ).isEqualTo( "Login" );
    }

    @Test
    void skips_when_application_present()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( APPLICATION_KEY_PROPERTY, "com.example.idprovider" );
        data.setSet( CONFIG_PROPERTY, data.newSet() );

        assertThat( upgrader.upgrade( SystemConstants.SYSTEM_REPO_ID, SYSTEM_ID_PROVIDER_PATH, createVersion( data ) ) ).isNull();
        assertThat( data.getString( APPLICATION_KEY_PROPERTY ) ).isEqualTo( "com.example.idprovider" );
    }

    @Test
    void skips_other_id_providers()
    {
        final NodePath otherIdProvider = NodePath.create( NodePath.ROOT ).addElement( "identity" ).addElement( "other" ).build();

        assertThat( upgrader.upgrade( SystemConstants.SYSTEM_REPO_ID, otherIdProvider, createVersion( new PropertyTree() ) ) ).isNull();
    }

    @Test
    void skips_other_repositories()
    {
        assertThat(
            upgrader.upgrade( RepositoryId.from( "com.enonic.cms.default" ), SYSTEM_ID_PROVIDER_PATH, createVersion( new PropertyTree() ) ) ).isNull();
    }

    @Test
    void skips_unknown_path()
    {
        assertThat( upgrader.upgrade( SystemConstants.SYSTEM_REPO_ID, null, createVersion( new PropertyTree() ) ) ).isNull();
    }

    private static NodeStoreVersion createVersion( final PropertyTree data )
    {
        data.setString( "displayName", "System Id Provider" );
        return NodeStoreVersion.create().id( NodeId.from( "system-id-provider" ) ).data( data ).build();
    }
}
