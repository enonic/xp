package com.enonic.wem.portal.internal.controller;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.thumb.Thumbnail;

import static org.junit.Assert.*;

public class JsControllerImplTest
    extends AbstractControllerTest
{
    @Test
    public void testExecute()
    {
        this.request.setMethod( "GET" );
        execute( "mymodule-1.0.0:/service/test" );
        assertEquals( JsHttpResponse.STATUS_OK, this.response.getStatus() );
    }

    @Test
    public void testExecutePostProcess()
    {
        this.request.setMethod( "GET" );
        this.response.setPostProcess( true );

        execute( "mymodule-1.0.0:/service/test" );

        assertEquals( JsHttpResponse.STATUS_OK, this.response.getStatus() );
        Mockito.verify( this.postProcessor ).processResponse( this.context );
    }

    @Test
    public void testMethodNotSupported()
    {
        this.request.setMethod( "POST" );
        execute( "mymodule-1.0.0:/service/test" );
        assertEquals( JsHttpResponse.STATUS_METHOD_NOT_ALLOWED, this.response.getStatus() );
    }

    @Test
    public void testGetterAccess()
    {
        final Page page = Page.newPage().template( PageTemplateKey.from( "mymodule|mypagetemplate" ) ).build();
        final Thumbnail thumbnail = Thumbnail.from( new BlobKey( "1234" ), "image/jpg", 1000 );

        final Content content =
            Content.newContent().name( "test" ).parentPath( ContentPath.ROOT ).page( page ).thumbnail( thumbnail ).build();
        this.context.setContent( content );

        this.request.setMethod( "GET" );
        execute( "mymodule-1.0.0:/service/getters" );
        assertEquals( JsHttpResponse.STATUS_OK, this.response.getStatus() );
        assertEquals( "GET,test,mymodule|mypagetemplate,1000", this.response.getBody() );
    }
}
