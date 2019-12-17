package com.enonic.xp.site;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.content.ContentTypeFilter;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentTypeFilterTypeTest
{
    @Test
    public void testContentFilterDeny()
        throws Exception
    {
        final ContentTypeFilter filter = ContentTypeFilter.create().
            defaultDeny().
            allowContentType( ContentTypeName.from( "myapplication:com.enonic.tweet" ) ).
            allowContentType( "myapplication:system.folder" ).
            allowContentTypes( ContentTypeNames.from( "myapplication:com.enonic.article", "myapplication:com.enonic.employee" ) ).
            build();

        assertEquals( ContentTypeFilter.AccessType.DENY, filter.getDefaultAccess() );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "myapplication:com.enonic.tweet" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "myapplication:system.folder" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "myapplication:com.enonic.article" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "myapplication:com.enonic.employee" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "myapplication:other" ) ) );
    }

    @Test
    public void testContentFilterAllow()
        throws Exception
    {
        final ContentTypeFilter filter = ContentTypeFilter.create().
            defaultAllow().
            denyContentType( ContentTypeName.from( "myapplication:com.enonic.tweet" ) ).
            denyContentType( "myapplication:system.folder" ).
            denyContentTypes( ContentTypeNames.from( "myapplication:com.enonic.article", "myapplication:com.enonic.employee" ) ).
            build();

        assertEquals( ContentTypeFilter.AccessType.ALLOW, filter.getDefaultAccess() );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "myapplication:com.enonic.tweet" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "myapplication:system.folder" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "myapplication:com.enonic.article" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "myapplication:com.enonic.employee" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "myapplication:other" ) ) );
    }

    @Test
    public void testContentFilterIteration()
        throws Exception
    {
        final ContentTypeFilter filter = ContentTypeFilter.create().
            defaultAllow().
            denyContentType( ContentTypeName.from( "myapplication:com.enonic.tweet" ) ).
            denyContentType( "myapplication:system.folder" ).
            denyContentTypes( ContentTypeNames.from( "myapplication:com.enonic.article", "myapplication:com.enonic.employee" ) ).
            build();

        final Iterator<ContentTypeName> iterator = filter.iterator();
        assertEquals( ContentTypeName.from( "myapplication:com.enonic.tweet" ), iterator.next() );
        assertEquals( ContentTypeName.from( "myapplication:system.folder" ), iterator.next() );
        assertEquals( ContentTypeName.from( "myapplication:com.enonic.article" ), iterator.next() );
        assertEquals( ContentTypeName.from( "myapplication:com.enonic.employee" ), iterator.next() );
        assertFalse( iterator.hasNext() );
    }
}
