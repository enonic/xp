package com.enonic.wem.core.index.elastic.searchsource;

import org.junit.Test;

import com.enonic.wem.api.content.query.ContentIndexQuery;

public class SearchSourceFactoryTest
{

    @Test
    public void testStuff()
        throws Exception
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( "test" );
    }
}
