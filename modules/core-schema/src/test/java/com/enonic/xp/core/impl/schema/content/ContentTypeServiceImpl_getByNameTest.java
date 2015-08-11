package com.enonic.xp.core.impl.schema.content;

import org.junit.Test;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static org.junit.Assert.*;

public class ContentTypeServiceImpl_getByNameTest
    extends AbstractContentTypeServiceTest
{
    @Test
    public void testGetByName()
        throws Exception
    {
        register( createContentType( "myapplication:my-contenttype", "DisplayName" ) );

        final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( "myapplication:my-contenttype" );
        final ContentType contentType = this.service.getByName( params );

        assertEquals( "myapplication:my-contenttype", contentType.getName().toString() );
        assertEquals( "DisplayName", contentType.getDisplayName() );
    }
}
