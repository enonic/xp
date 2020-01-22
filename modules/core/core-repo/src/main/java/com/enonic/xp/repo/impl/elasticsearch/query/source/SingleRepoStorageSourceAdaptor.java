package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.storage.BaseStorageName;
import com.enonic.xp.repo.impl.storage.JoinIndexPath;

class SingleRepoStorageSourceAdaptor
    extends AbstractSourceAdapter
{
    public static ESSource adapt( final SingleRepoStorageSource source )
    {
        final ESSource.Builder esSource = ESSource.create();

        final BaseStorageName storageIndexName = createStorageIndexName( source.getRepositoryId(), source.getType() );

        if ( storageIndexName != null )
        {
            esSource.addIndexName( storageIndexName.getName() );
        }

        final Value storageValue = SingleRepoStorageSource.Type.BRANCH == source.getType()
            ? ValueFactory.newString( JoinIndexPath.BRANCH_JOIN_NAME )
            : SingleRepoStorageSource.Type.VERSION == source.getType() ? ValueFactory.newString( JoinIndexPath.VERSION_JOIN_NAME ) : null;

        if ( storageValue != null )
        {
            esSource.addFilter( ValueFilter.create().
                fieldName( BranchIndexPath.JOIN_FIELD.getPath() ).
                addValue( storageValue ).
                build() );
        }

        return esSource.build();
    }
}
