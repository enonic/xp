package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.storage.BaseStorageName;

class SingleRepoStorageSourceAdaptor
    extends AbstractSourceAdapter
{
    public static ESSource adapt( final SingleRepoStorageSource source )
    {
        final ESSource.Builder esSource = ESSource.create().
            addIndexType( source.getType().name().toLowerCase() );

        final BaseStorageName storageIndexName = createStorageIndexName( source.getRepositoryId(), source.getType() );

        if ( storageIndexName != null )
        {
            esSource.addIndexName( storageIndexName.getName() );
        }

        return esSource.build();
    }
}
