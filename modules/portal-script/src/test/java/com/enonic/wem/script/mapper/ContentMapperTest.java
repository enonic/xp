package com.enonic.wem.script.mapper;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.script.AbstractMapSerializableTest;

public class ContentMapperTest
    extends AbstractMapSerializableTest
{
    @Test
    public void testSimple()
        throws Exception
    {
        final Content content = ContentFixtures.newContent();
        final ContentMapper mapper = new ContentMapper( content );

        assertJson( "simple", mapper );
    }
}

