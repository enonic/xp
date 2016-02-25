package com.enonic.xp.blobstore.swift;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.Access;
import org.openstack4j.openstack.OSFactory;

import com.google.common.base.Strings;

import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blobstore.swift.config.SwiftConfig;
import com.enonic.xp.util.ClassLoaderHelper;

class SwiftClientFactory
{
    public static Access create( final SwiftConfig config )
    {
        final Integer version = config.authVersion();

        if ( version == 3 )
        {
            return createV3Connection( config );
        }
        else if ( version == 2 )
        {
            return createV2Connection( config );
        }
        else
        {
            throw new BlobStoreException( "Unsupported auth-version: " + config.authVersion() );
        }
    }

    private static Access createV3Connection( final SwiftConfig config )
    {
        final OSClient os = ClassLoaderHelper.callWith( () -> {
            final Identifier domainIdentifier = getDomainIdentifier( config );

            return OSFactory.builderV3().
                endpoint( config.authUrl() ).
                credentials( config.authUser(), config.authPassword(), domainIdentifier ).
                scopeToProject( getProjectIdentifier( config ), domainIdentifier ).
                authenticate();
        }, OSFactory.class );

        return os != null ? os.getAccess() : null;
    }

    private static Access createV2Connection( final SwiftConfig config )
    {
        final OSClient os = ClassLoaderHelper.callWith( () -> OSFactory.builder().
            endpoint( config.authUrl() ).
            credentials( config.authUser(), config.authPassword() ).
            authenticate(), OSFactory.class );

        return os != null ? os.getAccess() : null;
    }

    private static Identifier getDomainIdentifier( final SwiftConfig config )
    {
        if ( !Strings.isNullOrEmpty( config.domainId() ) )
        {
            return Identifier.byId( config.domainId() );
        }

        return Identifier.byName( config.domainName() );
    }

    private static Identifier getProjectIdentifier( final SwiftConfig config )
    {
        if ( !Strings.isNullOrEmpty( config.projectId() ) )
        {
            return Identifier.byId( config.projectId() );
        }

        return Identifier.byName( config.projectName() );
    }
}
