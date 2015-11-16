package com.enonic.xp.web.impl.multipart;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.Part;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.web.multipart.MultipartItem;

import static org.junit.Assert.*;

public class MultipartFormImplTest
{
    private MultipartFormImpl form;

    private Part part1;

    private Part part2;

    @Before
    public void setup()
        throws Exception
    {
        this.part1 = Mockito.mock( Part.class );
        Mockito.when( this.part1.getName() ).thenReturn( "upload1" );
        Mockito.when( this.part1.getInputStream() ).thenReturn( newStream( "hello1" ) );

        this.part2 = Mockito.mock( Part.class );
        Mockito.when( this.part2.getName() ).thenReturn( "upload2" );
        Mockito.when( this.part2.getInputStream() ).thenReturn( newStream( "hello2" ) );

        this.form = new MultipartFormImpl( Lists.newArrayList( this.part1, this.part2 ) );
    }

    private InputStream newStream( final String text )
    {
        return new ByteArrayInputStream( text.getBytes() );
    }

    @Test
    public void testEmpty()
    {
        final MultipartFormImpl emptyForm = new MultipartFormImpl( Collections.emptyList() );

        final List<MultipartItem> items = Lists.newArrayList( emptyForm );
        assertEquals( 0, items.size() );
        assertEquals( 0, emptyForm.getSize() );
        assertEquals( true, emptyForm.isEmpty() );
    }

    @Test
    public void testIterate()
    {
        final List<MultipartItem> items = Lists.newArrayList( this.form );
        assertEquals( 2, items.size() );
        assertEquals( 2, this.form.getSize() );
        assertEquals( false, this.form.isEmpty() );
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

        Mockito.verify( this.part1, Mockito.times( 1 ) ).delete();
        Mockito.verify( this.part2, Mockito.times( 1 ) ).delete();
    }
}
