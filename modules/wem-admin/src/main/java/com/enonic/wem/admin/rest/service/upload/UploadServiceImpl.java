package com.enonic.wem.admin.rest.service.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;


public final class UploadServiceImpl
    implements UploadService
{
    private final File uploadDir;

    private final Map<String, UploadItem> itemMap;

    public UploadServiceImpl()
    {
        this.uploadDir = Files.createTempDir();
        this.itemMap = Maps.newConcurrentMap();
    }

    @Override
    public UploadItem upload( final String name, final String mediaType, final InputStream in )
        throws IOException
    {
        final File file = storeFile( in );
        final UploadItemImpl item = new UploadItemImpl( file, name, mediaType );
        this.itemMap.put( item.getId(), item );
        return item;
    }

    private File storeFile( final InputStream in )
        throws IOException
    {
        final File file = new File( this.uploadDir, UUID.randomUUID().toString() );
        ByteStreams.copy( in, Files.newOutputStreamSupplier( file ) );
        return file;
    }

    @Override
    public UploadItem getItem( final String id )
    {
        return this.itemMap.get( id );
    }

    @Override
    public void removeItem( final String id )
    {
        final UploadItem item = this.itemMap.remove( id );
        if ( item == null )
        {
            return;
        }

        FileUtils.deleteQuietly( item.getFile() );
    }
}
