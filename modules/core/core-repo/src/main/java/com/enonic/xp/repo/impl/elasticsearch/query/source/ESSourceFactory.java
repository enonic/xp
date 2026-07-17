package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.storage.spi.MultiRepoSearchSource;
import com.enonic.xp.storage.spi.SearchSource;
import com.enonic.xp.storage.spi.SingleRepoSearchSource;
import com.enonic.xp.storage.spi.SingleRepoStorageSource;

public class ESSourceFactory
{
    public static ESSource create( final SearchSource searchSource )
    {
        if ( searchSource instanceof SingleRepoSearchSource )
        {
            return SingleRepoSearchSourceAdaptor.adapt( (SingleRepoSearchSource) searchSource );
        }

        if ( searchSource instanceof SingleRepoStorageSource )
        {
            return SingleRepoStorageSourceAdaptor.adapt( (SingleRepoStorageSource) searchSource );
        }

        if ( searchSource instanceof MultiRepoSearchSource )
        {
            return MultiRepoSearchSourceAdaptor.adapt( (MultiRepoSearchSource) searchSource );
        }

        throw new IllegalArgumentException( "Not able to adapt datasource of type " + searchSource.getClass().getName() );
    }

}
