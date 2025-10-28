package com.enonic.xp.repo.impl.elasticsearch.query.source;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.SearchSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ESSourceFactoryTest
{
    @Test
    void unknown_impl()
        throws Exception
    {

        final SearchSource unknown = new SearchSource()
        {
        };

        assertThrows(IllegalArgumentException.class, () -> ESSourceFactory.create( unknown ));
    }
}
