package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.repo.impl.SingleRepoStorageSource;

class SingleRepoStorageSourceAdaptor
    extends AbstractSourceAdapter
{
    public static ESSource adapt( final SingleRepoStorageSource source )
    {
        return ESSource.create().
            addIndexType( source.getType().name().toLowerCase() ).
            addIndexName( createStorageIndexName( source.getRepositoryId() ) ).
            build();
    }
}
