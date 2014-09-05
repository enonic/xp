package com.enonic.wem.api.content.site;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.schema.content.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

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
            allowContentType( ContentTypeName.from( "mymodule-1.0.0:com.enonic.tweet" ) ).
            allowContentType( "mymodule-1.0.0:system.folder" ).
            allowContentTypes( ContentTypeNames.from( "mymodule-1.0.0:com.enonic.article", "mymodule-1.0.0:com.enonic.employee" ) ).
            build();

        assertEquals( ContentTypeFilter.AccessType.DENY, filter.getDefaultAccess() );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule-1.0.0:com.enonic.tweet" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule-1.0.0:system.folder" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule-1.0.0:com.enonic.article" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule-1.0.0:com.enonic.employee" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule-1.0.0:other" ) ) );
    }

    @Test
    public void testContentFilterAllow()
        throws Exception
    {
        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "mymodule-1.0.0:com.enonic.tweet" ) ).
            denyContentType( "mymodule-1.0.0:system.folder" ).
            denyContentTypes( ContentTypeNames.from( "mymodule-1.0.0:com.enonic.article", "mymodule-1.0.0:com.enonic.employee" ) ).
            build();

        assertEquals( ContentTypeFilter.AccessType.ALLOW, filter.getDefaultAccess() );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule-1.0.0:com.enonic.tweet" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule-1.0.0:system.folder" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule-1.0.0:com.enonic.article" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule-1.0.0:com.enonic.employee" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "mymodule-1.0.0:other" ) ) );
    }

    @Test
    public void testContentFilterIteration()
        throws Exception
    {
        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "mymodule-1.0.0:com.enonic.tweet" ) ).
            denyContentType( "mymodule-1.0.0:system.folder" ).
            denyContentTypes( ContentTypeNames.from( "mymodule-1.0.0:com.enonic.article", "mymodule-1.0.0:com.enonic.employee" ) ).
            build();

        final Iterator<ContentTypeName> iterator = filter.iterator();
        Assert.assertEquals( ContentTypeName.from( "mymodule-1.0.0:com.enonic.tweet" ), iterator.next() );
        Assert.assertEquals( ContentTypeName.from( "mymodule-1.0.0:system.folder" ), iterator.next() );
        Assert.assertEquals( ContentTypeName.from( "mymodule-1.0.0:com.enonic.article" ), iterator.next() );
        Assert.assertEquals( ContentTypeName.from( "mymodule-1.0.0:com.enonic.employee" ), iterator.next() );
        assertFalse( iterator.hasNext() );
    }
}
