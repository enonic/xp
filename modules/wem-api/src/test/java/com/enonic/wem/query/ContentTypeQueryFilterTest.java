package com.enonic.wem.query;

import org.junit.Test;

import com.enonic.wem.query.queryfilter.ContentTypeQueryFilter;

import static org.junit.Assert.*;

public class ContentTypeQueryFilterTest
{
    @Test
    public void build()
        throws Exception
    {
        final ContentTypeQueryFilter contentTypeFilter = ContentTypeQueryFilter.newContentTypeFilter().
            add( "contentType1", "contentType2" ).
            build();

        assertEquals( "contentType", contentTypeFilter.getFieldName() );
        assertEquals( 2, contentTypeFilter.getValues().size() );
    }
}
