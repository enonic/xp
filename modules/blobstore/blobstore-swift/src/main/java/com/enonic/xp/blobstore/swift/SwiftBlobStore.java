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

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeyCreator;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blobstore.swift.config.SwiftConfig;
import com.enonic.xp.util.Exceptions;

public class SwiftBlobStore
    implements BlobStore
{
    private final SwiftConfig config;

    private final String container;

    private final Access access;

    private final static Logger LOG = LoggerFactory.getLogger( SwiftBlobStore.class );

    public SwiftBlobStore( final SwiftConfig config )
    {
        this.config = config;
        this.container = config.container();
        this.access = connect();
        createContainer();
    }

    private Access connect()
    {
        final Access access = SwiftClientFactory.create( this.config );

        if ( access == null )
        {
            throw new BlobStoreException(
                "Cannot connect to blobstore [" + this.config.authUrl() + "] with user [" + this.config.authUser() + "]" );
        }

        LOG.info( "Connected to blobstore [" + access.getEndpoint() + "] successfully with user [" + access.getUser().getUsername() + "]" );

        return access;
    }

    private boolean createContainer()
    {
        if ( checkExists() )
        {
            return true;
        }

        return getClient().objectStorage().containers().create( this.container ).isSuccess();
    }

    private boolean checkExists()
    {
        final Map<String, String> metadata = getClient().objectStorage().containers().getMetadata( this.container );

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
            get( this.container, key.toString() ).
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
            this.getClient().objectStorage().
                objects().
                put( this.container, key.toString(), Payloads.create( stream ) );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to store blob", e );
        }

        return new SwiftBlobRecord( in, key );
    }

}
