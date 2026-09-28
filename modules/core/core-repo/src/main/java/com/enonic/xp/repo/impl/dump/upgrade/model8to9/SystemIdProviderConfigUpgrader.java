package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SystemConstants;

/**
 * Adds the standard id provider application config to the {@code system} id provider node when it is missing.
 * Very old installations have a {@code system} id provider without an application, which leaves the portal
 * without an interactive login.
 */
@NullMarked
public class SystemIdProviderConfigUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( SystemIdProviderConfigUpgrader.class );

    static final NodePath SYSTEM_ID_PROVIDER_PATH = NodePath.create( NodePath.ROOT )
        .addElement( PrincipalKey.IDENTITY_NODE_NAME )
        .addElement( IdProviderKey.system().toString() )
        .build();

    static final String APPLICATION_KEY_PROPERTY = "idProvider.applicationKey";

    static final String CONFIG_PROPERTY = "idProvider.config";

    static final String STANDARD_ID_PROVIDER_APPLICATION = "com.enonic.xp.app.standardidprovider";

    public @Nullable NodeStoreVersion upgrade( final RepositoryId repositoryId, final @Nullable NodePath nodePath,
                                               final NodeStoreVersion nodeVersion )
    {
        if ( !SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) || !SYSTEM_ID_PROVIDER_PATH.equals( nodePath ) )
        {
            return null;
        }

        final PropertyTree data = nodeVersion.data();
        if ( data.getString( APPLICATION_KEY_PROPERTY ) != null )
        {
            return null;
        }

        data.setString( APPLICATION_KEY_PROPERTY, STANDARD_ID_PROVIDER_APPLICATION );
        if ( data.getSet( CONFIG_PROPERTY ) == null )
        {
            data.setSet( CONFIG_PROPERTY, data.newSet() );
        }

        LOG.info( "Added [{}] application config to system id provider node [{}]", STANDARD_ID_PROVIDER_APPLICATION, nodeVersion.id() );
        return nodeVersion;
    }
}
