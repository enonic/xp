package com.enonic.xp.blobstore.swift;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.DLPayload;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.identity.Access;
import org.openstack4j.openstack.OSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.blob.AbstractBlobStore;
import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeyCreator;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.util.Exceptions;

public class SwiftBlobStore
    extends AbstractBlobStore
    implements BlobStore
{
    private final String authUrl;

    private final String authUser;

    private final String authPassword;

    private final String domainId;

    private final String domainName;

    private final String projectId;

    private final String projectName;

    private final Integer authVersion;

    private final Access access;

    private final static Logger LOG = LoggerFactory.getLogger( SwiftBlobStore.class );

    private SwiftBlobStore( final Builder builder )
    {
        super( builder );
        authUrl = builder.authUrl;
        authUser = builder.authUser;
        authPassword = builder.authPassword;
        domainId = builder.domainId;
        domainName = builder.domainName;
        projectId = builder.projectId;
        projectName = builder.projectName;
        this.authVersion = builder.authVersion;

        this.access = connect();
        createContainers();
    }

    private Access connect()
    {
        final Access access = SwiftClientFactory.create().
            authPassword( this.authPassword ).
            authUser( this.authUser ).
            authUrl( this.authUrl ).
            authVersion( this.authVersion ).
            domainId( this.domainId ).
            domainName( this.domainName ).
            projectId( this.projectId ).
            projectName( this.projectName ).
            build().
            execute();

        if ( access == null )
        {
            throw new BlobStoreException( "Cannot connect to blobstore [" + this.authUrl + "] with user [" + this.authUser + "]" );
        }

        LOG.info( "Connected to blobstore [" + access.getEndpoint() + "] successfully with user [" + access.getUser().getUsername() + "]" );

        return access;
    }

    private void createContainers()
    {
        for ( final String collection : this.segmentsCollectionMap )
        {
            if ( !containerExist( collection ) )
            {
                getClient().objectStorage().containers().create( collection ).isSuccess();
            }
        }
    }

    private boolean containerExist( final String collection )
    {
        final Map<String, String> metadata = getClient().objectStorage().containers().getMetadata( collection );

        final String timestamp = metadata.get( "X-Timestamp" );

        if ( timestamp != null )
        {
            return true;
        }
        return false;
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        final OSClient osClient = getClient();

        final DLPayload blob = osClient.objectStorage().
            objects().
            get( getCollectionName( segment ), key.toString() ).
            download();

        if ( blob == null )
        {
            return null;
        }

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

    private OSClient getClient()
    {
        return OSFactory.clientFromAccess( this.access );
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        final BlobKey key = BlobKeyCreator.createKey( in );

        return doAddRecord( segment, in, key );
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        return doAddRecord( segment, record.getBytes(), record.getKey() );
    }

    private BlobRecord doAddRecord( final Segment segment, final ByteSource in, final BlobKey key )
    {
        try (InputStream stream = in.openStream())
        {
            this.getClient().objectStorage().
                objects().
                put( getCollectionName( segment ), key.toString(), Payloads.create( stream ) );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to store blob", e );
        }

        return new SwiftBlobRecord( in, key );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractBlobStore.Builder<Builder>
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

        public Builder authVersion( final Integer authVersion )
        {
            this.authVersion = authVersion;
            return this;
        }

        public SwiftBlobStore build()
        {
            return new SwiftBlobStore( this );
        }
    }
}
