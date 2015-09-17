package com.enonic.xp.core.impl.schema.content;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.content.ContentTypes;

import static org.junit.Assert.*;

public class ContentTypeServiceImpl_getByApplicationTest
    extends AbstractContentTypeServiceTest
{
    @Test
    public void testGetByName()
        throws Exception
    {
        register( createContentType( "myapplication:my-contenttype", "DisplayName1" ),
                  createContentType( "othermodule:my-contenttype", "DisplayName2" ) );

        final ContentTypes result = this.service.getByApplication( ApplicationKey.from( "myapplication" ) );

        assertEquals( 1, result.getSize() );
        verifyContentType( "myapplication:my-contenttype", "DisplayName1", result );
    }
}
