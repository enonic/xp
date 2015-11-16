package com.enonic.xp.web.impl.multipart;

import java.io.ByteArrayInputStream;

import javax.servlet.http.Part;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class MultipartItemImplTest
{
    @Test
    public void testItem()
        throws Exception
    {
        final Part part = Mockito.mock( Part.class );
        Mockito.when( part.getName() ).thenReturn( "upload" );
        Mockito.when( part.getSubmittedFileName() ).thenReturn( "image.png" );
        Mockito.when( part.getContentType() ).thenReturn( "image/png" );
        Mockito.when( part.getSize() ).thenReturn( 10L );
        Mockito.when( part.getInputStream() ).thenReturn( new ByteArrayInputStream( "hello".getBytes() ) );

        final MultipartItemImpl item = new MultipartItemImpl( part );

        assertEquals( "upload", item.getName() );
        assertEquals( "image.png", item.getFileName() );
        assertEquals( "image/png", item.getContentType().toString() );
        assertEquals( false, item.isEmpty() );
        assertEquals( 10, item.getSize() );
        assertEquals( 10, item.size() );
        assertNotNull( item.getBytes() );
        assertEquals( "hello", item.getAsString() );
    }
}
