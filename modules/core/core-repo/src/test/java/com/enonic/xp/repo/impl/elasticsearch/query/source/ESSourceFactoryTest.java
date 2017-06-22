package com.enonic.xp.repo.impl.elasticsearch.query.source;

import org.junit.Test;

import com.enonic.xp.repo.impl.SearchSource;

public class ESSourceFactoryTest
{
    @Test(expected = IllegalArgumentException.class)
    public void unknown_impl()
        throws Exception
    {

        final SearchSource unknown = new SearchSource()
        {
        };

        ESSourceFactory.create( unknown );
    }
}