package com.enonic.xp.repo.impl.elasticsearch.query.source;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.MultiRepoSearchSource;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKeys;

class RepoBranchAclMap
    implements Iterable<RepositoryId>
{
    private Multimap<RepositoryId, BranchAclEntry> repoAclMap;

    private RepoBranchAclMap( final Builder builder )
    {
        repoAclMap = builder.repoAclMap;
    }

    public static RepoBranchAclMap from( final MultiRepoSearchSource source )
    {
        final Builder builder = RepoBranchAclMap.create();
        source.getSources().forEach( ( entry ) -> builder.add( entry.getRepositoryId(), entry.getBranch(), entry.getAcl() ) );
        return builder.build();
    }

    @Override
    public Iterator<RepositoryId> iterator()
    {
        return this.repoAclMap.keySet().iterator();
    }

    Collection<BranchAclEntry> getBranchAclEntries( final RepositoryId repositoryId )
    {
        return this.repoAclMap.get( repositoryId );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Multimap<RepositoryId, BranchAclEntry> repoAclMap = ArrayListMultimap.create();

        private Builder()
        {
        }

        public Builder add( final RepositoryId repoId, final Branch branch, final PrincipalKeys acl )
        {
            repoAclMap.put( repoId, new BranchAclEntry( branch, acl ) );
            return this;
        }

        public RepoBranchAclMap build()
        {
            return new RepoBranchAclMap( this );
        }
    }
}
