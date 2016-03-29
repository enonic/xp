package com.enonic.xp.blobstore.swift;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.Access;
import org.openstack4j.openstack.OSFactory;

import com.google.common.base.Strings;

import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.util.ClassLoaderHelper;

class SwiftClientFactory
{
    private final String authUrl;

    private final String authUser;

    private final String authPassword;

    private final String domainId;

    private final String domainName;

    private final String projectId;

    private final String projectName;

    private final Integer authVersion;

    private SwiftClientFactory( final Builder builder )
    {
        authUrl = builder.authUrl;
        authUser = builder.authUser;
        authPassword = builder.authPassword;
        domainId = builder.domainId;
        domainName = builder.domainName;
        projectId = builder.projectId;
        projectName = builder.projectName;
        authVersion = builder.authVersion;
    }

    public Access execute()
    {
        final Integer version = this.authVersion;

        if ( version == 3 )
        {
            return createV3Connection();
        }
        else if ( version == 2 )
        {
            return createV2Connection();
        }
        else
        {
            throw new BlobStoreException( "Unsupported auth-version: " + this.authVersion );
        }
    }

    private Access createV3Connection()
    {
        final OSClient os = ClassLoaderHelper.callWith( () -> {
            final Identifier domainIdentifier = getDomainIdentifier();

            return OSFactory.builderV3().
                endpoint( this.authUrl ).
                credentials( this.authUser, this.authPassword, domainIdentifier ).
                scopeToProject( getProjectIdentifier(), domainIdentifier ).
                authenticate();
        }, OSFactory.class );

        return os != null ? os.getAccess() : null;
    }

    private Access createV2Connection()
    {
        final OSClient os = ClassLoaderHelper.callWith( () -> OSFactory.builder().
            endpoint( this.authUrl ).
            credentials( this.authUser, this.authPassword ).
            authenticate(), OSFactory.class );

        return os != null ? os.getAccess() : null;
    }

    private Identifier getDomainIdentifier()
    {
        if ( !Strings.isNullOrEmpty( this.domainId ) )
        {
            return Identifier.byId( this.domainId );
        }

        return Identifier.byName( this.domainName );
    }

    private Identifier getProjectIdentifier()
    {
        if ( !Strings.isNullOrEmpty( this.projectId ) )
        {
            return Identifier.byId( this.projectId );
        }

        return Identifier.byName( this.projectName );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String authUrl;

        private String authUser;

        private String authPassword;

        private String domainId;

        private String domainName;

        private String projectId;

        private String projectName;

        private Integer authVersion;

        private Builder()
        {
        }

        public Builder authUrl( final String val )
        {
            authUrl = val;
            return this;
        }

        public Builder authUser( final String val )
        {
            authUser = val;
            return this;
        }

        public Builder authPassword( final String val )
        {
            authPassword = val;
            return this;
        }

        public Builder domainId( final String val )
        {
            domainId = val;
            return this;
        }

        public Builder domainName( final String val )
        {
            domainName = val;
            return this;
        }

        public Builder projectId( final String val )
        {
            projectId = val;
            return this;
        }

        public Builder projectName( final String val )
        {
            projectName = val;
            return this;
        }

        public Builder authVersion( final Integer val )
        {
            authVersion = val;
            return this;
        }

        public SwiftClientFactory build()
        {
            return new SwiftClientFactory( this );
        }
    }
}
