package com.enonic.xp.portal.jslib.impl.mapper;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.xp.script.impl.AbstractMapSerializableTest;
import com.enonic.xp.portal.jslib.impl.ContentFixtures;

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

