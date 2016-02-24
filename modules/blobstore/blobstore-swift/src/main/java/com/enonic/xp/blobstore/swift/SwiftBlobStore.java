package com.enonic.xp.blobstore.swift;

import java.io.IOException;
import java.io.InputStream;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.DLPayload;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.compute.ActionResponse;
import org.openstack4j.model.identity.Access;
import org.openstack4j.openstack.OSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeyCreator;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.util.ClassLoaderHelper;
import com.enonic.xp.util.Exceptions;

public class SwiftBlobStore
    implements BlobStore
{
    final OSClient client;

    final String container;

    final String endpoint;

    final String domain;

    final String user;

    final String password;

    final String projectId;

    final Access access;

    private final static Logger LOG = LoggerFactory.getLogger( SwiftBlobStore.class );

    private SwiftBlobStore( final Builder builder )
    {
        projectId = builder.projectId;
        password = builder.password;
        user = builder.user;
        domain = builder.domain;
        endpoint = builder.endpoint;
        container = builder.container;
        this.client = createClient();
        this.access = this.client.getAccess();
    }

    public static Builder create()
    {
        return new Builder();
    }

    private OSClient createClient()
    {
        Identifier domainIdentifier = Identifier.byName( this.domain );

        final OSClient os = ClassLoaderHelper.callWith( () -> OSFactory.builderV3().
            endpoint( this.endpoint ).
            credentials( this.user, this.password, domainIdentifier ).
            scopeToProject( Identifier.byId( this.projectId ), domainIdentifier ).
            authenticate(), OSFactory.class );

        Access access = os.getAccess();

        LOG.info( "Connected to blobstore [" + this.endpoint + "] successfully with user [" + access.getUser().getUsername() + "]" );

        return os;
    }

    private boolean createContainer()
    {
        final ActionResponse response = this.client.objectStorage().containers().create( container );

        return response.isSuccess();
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        final DLPayload blob = this.client.objectStorage().
            objects().
            get( this.container, key.toString() ).
            download();

        if ( blob == null )
        {
            return null;
        }

        LOG.info( "Fetched resources with key [" + key + "] from swift blobstore" );

        try (final InputStream inputStream = blob.getInputStream())
        {
            final ByteSource source = ByteSource.wrap( ByteStreams.toByteArray( inputStream ) );

            return new SwiftBlobRecord( source, key );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        final BlobKey key = BlobKeyCreator.createKey( in );

        return doAddRecord( in, key );
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        return doAddRecord( record.getBytes(), record.getKey() );
    }

    private BlobRecord doAddRecord( final ByteSource in, final BlobKey key )
    {
        try (InputStream stream = in.openStream())
        {
            String etag = this.client.objectStorage().
                objects().
                put( this.container, key.toString(), Payloads.create( stream ) );

            LOG.info( "Fetched resources with key [" + key + "] and etag [" + etag + "] from swift blobstore" );

        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to store blob", e );
        }

        return new SwiftBlobRecord( in, key );
    }


    public static final class Builder
    {
        private String container;

        private String endpoint;

        private String domain;

        private String user;

        private String password;

        private String projectId;

        private Builder()
        {
        }

        public Builder container( final String val )
        {
            container = val;
            return this;
        }

        public Builder endpoint( final String val )
        {
            endpoint = val;
            return this;
        }

        public Builder domain( final String val )
        {
            domain = val;
            return this;
        }

        public Builder user( final String val )
        {
            user = val;
            return this;
        }

        public Builder password( final String val )
        {
            password = val;
            return this;
        }

        public SwiftBlobStore build()
        {
            return new SwiftBlobStore( this );
        }

        public Builder projectId( final String val )
        {
            projectId = val;
            return this;
        }
    }
}
