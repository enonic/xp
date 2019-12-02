package com.enonic.xp.repo.impl.elasticsearch.query.source;

import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AclFilterBuilderFactory;
import com.enonic.xp.security.PrincipalKeys;

class SingleRepoSearchSourceAdaptor
    extends AbstractSourceAdapter
{
    public static ESSource adapt( final SingleRepoSearchSource source )
    {
        return ESSource.create().
            addIndexName( createSearchIndexName( source.getRepositoryId(), source.getBranch() ) ).
            addFilter( createAclFilterBuilder( source.getAcl() ) ).
            build();
    }

    private static Filter createAclFilterBuilder( final PrincipalKeys keys )
    {
        return AclFilterBuilderFactory.create( keys );
    }

}
