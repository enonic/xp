package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.repo.impl.SearchSource;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;

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

        throw new IllegalArgumentException( "Not able to adapt datasource of type " + searchSource.getClass().getName() );
    }

}
