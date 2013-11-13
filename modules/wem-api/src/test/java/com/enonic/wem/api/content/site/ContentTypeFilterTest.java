package com.enonic.wem.api.content.site;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

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
            allowContentType( ContentTypeName.from( "com.enonic.tweet" ) ).
            allowContentType( "system.folder" ).
            allowContentTypes( ContentTypeNames.from( "com.enonic.article", "com.enonic.employee" ) ).
            build();

        assertEquals( ContentTypeFilter.AccessType.DENY, filter.getDefaultAccess() );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "com.enonic.tweet" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "system.folder" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "com.enonic.article" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "com.enonic.employee" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "other" ) ) );
    }

    @Test
    public void testContentFilterAllow()
        throws Exception
    {
        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "com.enonic.tweet" ) ).
            denyContentType( "system.folder" ).
            denyContentTypes( ContentTypeNames.from( "com.enonic.article", "com.enonic.employee" ) ).
            build();

        assertEquals( ContentTypeFilter.AccessType.ALLOW, filter.getDefaultAccess() );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "com.enonic.tweet" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "system.folder" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "com.enonic.article" ) ) );
        assertFalse( filter.isContentTypeAllowed( ContentTypeName.from( "com.enonic.employee" ) ) );
        assertTrue( filter.isContentTypeAllowed( ContentTypeName.from( "other" ) ) );
    }

    @Test
    public void testContentFilterIteration()
        throws Exception
    {
        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "com.enonic.tweet" ) ).
            denyContentType( "system.folder" ).
            denyContentTypes( ContentTypeNames.from( "com.enonic.article", "com.enonic.employee" ) ).
            build();

        final Iterator<ContentTypeName> iterator = filter.iterator();
        Assert.assertEquals( ContentTypeName.from( "com.enonic.tweet" ), iterator.next() );
        Assert.assertEquals( ContentTypeName.from( "system.folder" ), iterator.next() );
        Assert.assertEquals( ContentTypeName.from( "com.enonic.article" ), iterator.next() );
        Assert.assertEquals( ContentTypeName.from( "com.enonic.employee" ), iterator.next() );
        assertFalse( iterator.hasNext() );
    }
}
