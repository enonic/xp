package com.enonic.xp.core.content.site;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.core.schema.content.ContentTypeFilter;
import com.enonic.xp.core.schema.content.ContentTypeName;
import com.enonic.xp.core.schema.content.ContentTypeNames;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class ContentTypeFilterTest
{
    @Test
    public void testContentFilterDeny()
        throws Exception
    {
        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultDeny().
            allowContentType( ContentTypeName.from( "mymodule:com.enonic.tweet" ) ).
            allowContentType( "mymodule:system.folder" ).
            allowContentTypes( ContentTypeNames.from( "mymodule:com.enonic.article", "mymodule:com.enonic.employee" ) ).
            build();

        assertEquals( ContentTypeFilter.AccessType.DENY, filter.getDefaultAccess() );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule:com.enonic.tweet" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule:system.folder" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule:com.enonic.article" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule:com.enonic.employee" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule:other" ) ) );
    }

    @Test
    public void testContentFilterAllow()
        throws Exception
    {
        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "mymodule:com.enonic.tweet" ) ).
            denyContentType( "mymodule:system.folder" ).
            denyContentTypes( ContentTypeNames.from( "mymodule:com.enonic.article", "mymodule:com.enonic.employee" ) ).
            build();

        assertEquals( ContentTypeFilter.AccessType.ALLOW, filter.getDefaultAccess() );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule:com.enonic.tweet" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule:system.folder" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule:com.enonic.article" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule:com.enonic.employee" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule:other" ) ) );
    }

    @Test
    public void testContentFilterIteration()
        throws Exception
    {
        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "mymodule:com.enonic.tweet" ) ).
            denyContentType( "mymodule:system.folder" ).
            denyContentTypes( ContentTypeNames.from( "mymodule:com.enonic.article", "mymodule:com.enonic.employee" ) ).
            build();

        final Iterator<ContentTypeName> iterator = filter.iterator();
        Assert.assertEquals( ContentTypeName.from( "mymodule:com.enonic.tweet" ), iterator.next() );
        Assert.assertEquals( ContentTypeName.from( "mymodule:system.folder" ), iterator.next() );
        Assert.assertEquals( ContentTypeName.from( "mymodule:com.enonic.article" ), iterator.next() );
        Assert.assertEquals( ContentTypeName.from( "mymodule:com.enonic.employee" ), iterator.next() );
        assertFalse( iterator.hasNext() );
    }
}
