package com.enonic.xp.web.impl.multipart;

import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static org.junit.Assert.*;

public class MultipartFormImplTest
{
    private MultipartForm form;

    private FileItem fileItem1;

    private FileItem fileItem2;

    @Before
    public void setup()
    {
        this.fileItem1 = Mockito.mock( FileItem.class );
        Mockito.when( this.fileItem1.getFieldName() ).thenReturn( "upload1" );
        Mockito.when( this.fileItem1.get() ).thenReturn( "hello1".getBytes() );

        this.fileItem2 = Mockito.mock( FileItem.class );
        Mockito.when( this.fileItem2.getFieldName() ).thenReturn( "upload2" );
        Mockito.when( this.fileItem2.get() ).thenReturn( "hello2".getBytes() );

        this.form = new MultipartFormImpl( ImmutableList.of( this.fileItem1, this.fileItem2 ) );
    }

    @Test
    public void testIterate()
    {
        final List<MultipartItem> items = Lists.newArrayList( this.form );
        assertEquals( 2, items.size() );
    }

    @Test
    public void testGet()
    {
        final MultipartItem item1 = this.form.get( "upload1" );
        assertNotNull( item1 );
        assertEquals( "upload1", item1.getName() );

        final MultipartItem item2 = this.form.get( "upload2" );
        assertNotNull( item2 );
        assertEquals( "upload2", item2.getName() );

        final MultipartItem item3 = this.form.get( "upload3" );
        assertNull( item3 );
    }

    @Test
    public void testGetAsString()
        throws Exception
    {
        assertEquals( "hello1", this.form.getAsString( "upload1" ) );
        assertEquals( "hello2", this.form.getAsString( "upload2" ) );
        assertNull( this.form.getAsString( "upload3" ) );
    }

    @Test
    public void testDelete()
        throws Exception
    {
        this.form.delete();

        Mockito.verify( this.fileItem1, Mockito.times( 1 ) ).delete();
        Mockito.verify( this.fileItem2, Mockito.times( 1 ) ).delete();
    }
}
