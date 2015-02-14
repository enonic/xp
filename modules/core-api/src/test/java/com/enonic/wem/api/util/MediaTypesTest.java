package com.enonic.wem.api.util;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.net.MediaType;

public class MediaTypesTest
{
    @Test
    public void resolve_file_extension()
    {
        final MediaTypes mediaTypes = MediaTypes.instance();
        mediaTypes.clear();
        mediaTypes.put( "html", MediaType.HTML_UTF_8 );

        Assert.assertNotNull( mediaTypes.fromExt( "html" ) );
        Assert.assertEquals( "text/html", mediaTypes.fromExt( "html" ).toString() );

        Assert.assertNotNull( mediaTypes.fromExt( "any" ) );
        Assert.assertEquals( "application/octet-stream", mediaTypes.fromExt( "any" ).toString() );
    }

    @Test
    public void resolve_file_name()
    {
        final MediaTypes mediaTypes = MediaTypes.instance();
        mediaTypes.clear();
        mediaTypes.put( "html", MediaType.HTML_UTF_8 );

        Assert.assertNotNull( mediaTypes.fromFile( "index.html" ) );
        Assert.assertEquals( "text/html", mediaTypes.fromFile( "index.html" ).toString() );

        Assert.assertNotNull( mediaTypes.fromFile( "file" ) );
        Assert.assertEquals( "application/octet-stream", mediaTypes.fromFile( "file" ).toString() );
    }
}
