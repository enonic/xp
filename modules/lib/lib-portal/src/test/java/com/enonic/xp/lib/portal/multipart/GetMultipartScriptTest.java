package com.enonic.xp.lib.portal.multipart;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;
import com.enonic.xp.web.multipart.MultipartService;

public class GetMultipartScriptTest
    extends ScriptTestSupport
{
    private MultipartService multipartService;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.multipartService = Mockito.mock( MultipartService.class );
        addService( MultipartService.class, this.multipartService );
    }

    private void createForm()
    {
        final MultipartForm form = Mockito.mock( MultipartForm.class );

        final MultipartItem item1 = createItem( "item1", 10, "jpg", "image/png" );
        final MultipartItem item2 = createItem( "item2", 20, "jpg", "image/png" );

        Mockito.when( form.iterator() ).thenReturn( List.of( item1, item2 ).iterator() );
        Mockito.when( form.get( "item1", 0 ) ).thenReturn( item1 );
        Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );
    }

    private void createFormWithDuplicates()
    {
        final MultipartForm form = Mockito.mock( MultipartForm.class );

        final MultipartItem item1A = createItem( "file1", "text123", 10, "txt", "text/plain" );
        final MultipartItem item1B = createItem( "file1", "text456", 42, "txt", "application/json" );
        final MultipartItem item2 = createItem( "file2", 20, "jpg", "image/png" );

        Mockito.when( item1A.getAsString() ).thenReturn( "Some text" );
        Mockito.when( item1B.getAsString() ).thenReturn( "Other stuff" );

        Mockito.when( form.iterator() ).thenReturn( List.of( item1A, item1B, item2 ).iterator() );
        Mockito.when( form.get( "file1", 0 ) ).thenReturn( item1A );
        Mockito.when( form.get( "file1", 1 ) ).thenReturn( item1B );
        Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );
    }

    private void createFormExample()
    {
        final MultipartForm form = Mockito.mock( MultipartForm.class );

        final MultipartItem item1 = createItem( "item1", 10, "jpg", "image/png" );
        final MultipartItem item2A = createItem( "item2", "image1", 123, "png", "image/png" );
        final MultipartItem item2B = createItem( "item2", "image2", 456, "jpg", "image/jpeg" );

        Mockito.when( form.iterator() ).thenReturn( List.of( item1, item2A, item2B ).iterator() );
        Mockito.when( form.get( "item2", 0 ) ).thenReturn( item2A );
        Mockito.when( form.get( "item2", 1 ) ).thenReturn( item2B );
        Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );
    }

    private void createTextForm()
    {
        final MultipartForm form = Mockito.mock( MultipartForm.class );

        final MultipartItem item1 = createItem( "item1", 10, "txt", "text/plain" );
        Mockito.when( item1.getAsString() ).thenReturn( "Some text" );

        Mockito.when( form.iterator() ).thenReturn( List.of( item1 ).iterator() );
        Mockito.when( form.get( "item1", 0 ) ).thenReturn( item1 );
        Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );
    }

    private MultipartItem createItem( final String name, final long size, final String ext, final String type )
    {
        return createItem( name, name, size, ext, type );
    }

    private MultipartItem createItem( final String name, final String fileName, final long size, final String ext, final String type )
    {
        final MultipartItem item = Mockito.mock( MultipartItem.class );
        Mockito.when( item.getName() ).thenReturn( name );
        Mockito.when( item.getFileName() ).thenReturn( fileName + "." + ext );
        Mockito.when( item.getContentType() ).thenReturn( MediaType.parse( type ) );
        Mockito.when( item.getSize() ).thenReturn( size );
        Mockito.when( item.getBytes() ).thenReturn( ByteSource.wrap( name.getBytes() ) );
        return item;
    }

    @Test
    public void getForm()
    {
        createForm();
        runFunction( "/test/multipart-test.js", "getForm" );
    }

    @Test
    public void getFormWithDuplicates()
    {
        createFormWithDuplicates();
        runFunction( "/test/multipart-test.js", "getFormWithDuplicates" );
    }

    @Test
    public void getForm_empty()
    {
        runFunction( "/test/multipart-test.js", "getForm_empty" );
    }

    @Test
    public void getBytes()
    {
        createForm();
        runFunction( "/test/multipart-test.js", "getBytes" );
    }

    @Test
    public void getBytesMultiple()
    {
        createFormWithDuplicates();
        runFunction( "/test/multipart-test.js", "getBytesMultiple" );
    }

    @Test
    public void getBytes_notFound()
    {
        runFunction( "/test/multipart-test.js", "getBytes_notFound" );
    }

    @Test
    public void getItem()
    {
        createForm();
        runFunction( "/test/multipart-test.js", "getItem" );
    }

    @Test
    public void getItemMultiple()
    {
        createFormWithDuplicates();
        runFunction( "/test/multipart-test.js", "getItemMultiple" );
    }

    @Test
    public void getItem_notFound()
    {
        runFunction( "/test/multipart-test.js", "getItem_notFound" );
    }

    @Test
    public void getText()
    {
        createTextForm();
        runFunction( "/test/multipart-test.js", "getText" );
    }

    @Test
    public void getTextMultiple()
    {
        createFormWithDuplicates();
        runFunction( "/test/multipart-test.js", "getTextMultiple" );
    }

    @Test
    public void getText_notFound()
    {
        createForm();
        runFunction( "/test/multipart-test.js", "getText_notFound" );
    }

    @Test
    public void testExample_getMultipartForm()
    {
        createFormExample();
        runScript( "/lib/xp/examples/portal/getMultipartForm.js" );
    }

    @Test
    public void testExample_getMultipartItem()
    {
        createForm();
        runScript( "/lib/xp/examples/portal/getMultipartItem.js" );
    }

    @Test
    public void testExample_getMultipartStream()
    {
        createFormExample();
        runScript( "/lib/xp/examples/portal/getMultipartStream.js" );
    }

    @Test
    public void testExample_getMultipartText()
    {
        createTextForm();
        runScript( "/lib/xp/examples/portal/getMultipartText.js" );
    }
}
