package com.enonic.xp.web.impl.multipart;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.enonic.xp.web.multipart.MultipartItem;

import static org.junit.Assert.*;

public class MultipartItemImplTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testInMemory()
    {
        final FileItem fileItem = Mockito.mock( FileItem.class );
        Mockito.when( fileItem.getName() ).thenReturn( "image.png" );
        Mockito.when( fileItem.getFieldName() ).thenReturn( "upload" );
        Mockito.when( fileItem.getContentType() ).thenReturn( "image/png" );
        Mockito.when( fileItem.getSize() ).thenReturn( 10L );
        Mockito.when( fileItem.get() ).thenReturn( "hello".getBytes() );
        Mockito.when( fileItem.isInMemory() ).thenReturn( true );

        final MultipartItem item = new MultipartItemImpl( fileItem );

        assertEquals( "image.png", item.getFileName() );
        assertEquals( "upload", item.getName() );
        assertEquals( "image/png", item.getContentType().toString() );
        assertEquals( 10, item.getSize() );
        assertNotNull( item.getBytes() );
        assertEquals( "hello", item.getAsString() );
    }

    @Test
    public void testOnDisk()
    {
        final DiskFileItem fileItem = Mockito.mock( DiskFileItem.class );
        Mockito.when( fileItem.getName() ).thenReturn( "image.png" );
        Mockito.when( fileItem.getFieldName() ).thenReturn( "upload" );
        Mockito.when( fileItem.getContentType() ).thenReturn( "image/png" );
        Mockito.when( fileItem.getSize() ).thenReturn( 10L );
        Mockito.when( fileItem.get() ).thenReturn( "hello".getBytes() );
        Mockito.when( fileItem.isInMemory() ).thenReturn( false );
        Mockito.when( fileItem.getStoreLocation() ).thenReturn( this.folder.getRoot() );

        final MultipartItem item = new MultipartItemImpl( fileItem );

        assertEquals( "image.png", item.getFileName() );
        assertEquals( "upload", item.getName() );
        assertEquals( "image/png", item.getContentType().toString() );
        assertEquals( 10, item.getSize() );
        assertNotNull( item.getBytes() );
        assertEquals( "hello", item.getAsString() );
    }
}
