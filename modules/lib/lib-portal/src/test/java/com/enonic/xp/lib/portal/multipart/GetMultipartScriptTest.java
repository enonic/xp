package com.enonic.xp.lib.portal.multipart;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.testing.script.ScriptTestSupport;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;
import com.enonic.xp.web.multipart.MultipartService;

public class GetMultipartScriptTest
    extends ScriptTestSupport
{
    private MultipartService multipartService;

    @Override
    protected void initialize()
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

        Mockito.when( form.iterator() ).thenReturn( Lists.newArrayList( item1, item2 ).iterator() );
        Mockito.when( form.get( "item1" ) ).thenReturn( item1 );
        Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );
    }

    private void createTextForm()
    {
        final MultipartForm form = Mockito.mock( MultipartForm.class );

        final MultipartItem item1 = createItem( "item1", 10, "txt", "text/plain" );
        Mockito.when( item1.getAsString() ).thenReturn( "Some text" );

        Mockito.when( form.iterator() ).thenReturn( Lists.newArrayList( item1 ).iterator() );
        Mockito.when( form.get( "item1" ) ).thenReturn( item1 );
        Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );
    }

    private MultipartItem createItem( final String name, final long size, final String ext, final String type )
    {
        final MultipartItem item = Mockito.mock( MultipartItem.class );
        Mockito.when( item.getName() ).thenReturn( name );
        Mockito.when( item.getFileName() ).thenReturn( name + "." + ext );
        Mockito.when( item.getContentType() ).thenReturn( MediaType.parse( type ) );
        Mockito.when( item.getSize() ).thenReturn( size );
        Mockito.when( item.getBytes() ).thenReturn( ByteSource.wrap( name.getBytes() ) );
        return item;
    }

    @Test
    public void getForm()
    {
        createForm();
        runFunction( "/site/test/multipart-test.js", "getForm" );
    }

    @Test
    public void getForm_empty()
    {
        runFunction( "/site/test/multipart-test.js", "getForm_empty" );
    }

    @Test
    public void getBytes()
    {
        createForm();
        runFunction( "/site/test/multipart-test.js", "getBytes" );
    }

    @Test
    public void getBytes_notFound()
    {
        runFunction( "/site/test/multipart-test.js", "getBytes_notFound" );
    }

    @Test
    public void getItem()
    {
        createForm();
        runFunction( "/site/test/multipart-test.js", "getItem" );
    }

    @Test
    public void getItem_notFound()
    {
        runFunction( "/site/test/multipart-test.js", "getItem_notFound" );
    }

    @Test
    public void getText()
    {
        createTextForm();
        runFunction( "/site/test/multipart-test.js", "getText" );
    }

    @Test
    public void getText_notFound()
    {
        createForm();
        runFunction( "/site/test/multipart-test.js", "getText_notFound" );
    }

}
