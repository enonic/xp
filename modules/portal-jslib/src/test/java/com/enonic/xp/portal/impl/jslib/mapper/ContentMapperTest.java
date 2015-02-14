package com.enonic.xp.portal.impl.jslib.mapper;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.portal.impl.script.AbstractMapSerializableTest;
import com.enonic.xp.portal.impl.jslib.ContentFixtures;

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

