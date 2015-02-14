package com.enonic.xp.core.impl.schema.content;

import org.junit.Test;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.GetContentTypeParams;

import static org.junit.Assert.*;

public class ContentTypeServiceImpl_getByNameTest
    extends AbstractContentTypeServiceTest
{
    @Test
    public void testGetByName()
        throws Exception
    {
        register( createContentType( "mymodule:my-contenttype", "DisplayName" ) );

        final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( "mymodule:my-contenttype" );
        final ContentType contentType = this.service.getByName( params );

        assertEquals( "mymodule:my-contenttype", contentType.getName().toString() );
        assertEquals( "DisplayName", contentType.getDisplayName() );
    }
}
