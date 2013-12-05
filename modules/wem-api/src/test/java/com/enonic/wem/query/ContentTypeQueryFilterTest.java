package com.enonic.wem.query;

import org.junit.Test;

import com.enonic.wem.query.filter.ContentTypeFilter;

import static org.junit.Assert.*;

public class ContentTypeQueryFilterTest
{
    @Test
    public void build()
        throws Exception
    {
        final ContentTypeFilter contentTypeFilter = ContentTypeFilter.newContentTypeFilter().
            add( "contentType1", "contentType2" ).
            build();

        assertEquals( "contentType", contentTypeFilter.getFieldName() );
        assertEquals( 2, contentTypeFilter.getValues().size() );
    }
}
