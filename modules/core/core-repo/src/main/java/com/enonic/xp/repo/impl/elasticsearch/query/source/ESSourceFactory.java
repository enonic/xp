package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.repo.impl.DataSource;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;

public class ESSourceFactory
{
    public static ESSource create( final DataSource dataSource )
    {
        if ( dataSource instanceof SingleRepoSearchSource )
        {
            return SingleRepoSearchSourceAdaptor.adapt( (SingleRepoSearchSource) dataSource );
        }

        if ( dataSource instanceof SingleRepoStorageSource )
        {
            return SingleRepoStorageSourceAdaptor.adapt( (SingleRepoStorageSource) dataSource );
        }

        throw new IllegalArgumentException( "Not able to adapt datasource of type " + dataSource.getClass().getName() );
    }

}
