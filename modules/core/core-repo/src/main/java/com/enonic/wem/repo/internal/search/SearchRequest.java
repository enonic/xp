package com.enonic.wem.repo.internal.search;

import com.enonic.wem.repo.internal.ReturnFields;
import com.enonic.wem.repo.internal.StorageSettings;
import com.enonic.xp.query.Query;
import com.enonic.xp.security.PrincipalKeys;

public class SearchRequest
{
    private final StorageSettings settings;

    private final Query query;

    private final ReturnFields returnFields;

    private final PrincipalKeys acl;

    private SearchRequest( Builder builder )
    {
        settings = builder.settings;
        query = builder.query;
        returnFields = builder.returnFields;
        this.acl = builder.acl;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public StorageSettings getSettings()
    {
        return settings;
    }

    public PrincipalKeys getAcl()
    {
        return acl;
    }

    public Query getQuery()
    {
        return query;
    }

    public ReturnFields getReturnFields()
    {
        return returnFields;
    }

    public static final class Builder
    {
        private StorageSettings settings;

        private Query query;

        private ReturnFields returnFields;

        private PrincipalKeys acl;

        private Builder()
        {
        }

        public Builder settings( StorageSettings settings )
        {
            this.settings = settings;
            return this;
        }

        public Builder query( Query query )
        {
            this.query = query;
            return this;
        }

        public Builder returnFields( final ReturnFields returnFields )
        {
            this.returnFields = returnFields;
            return this;
        }

        public Builder acl( final PrincipalKeys acl )
        {
            this.acl = acl;
            return this;
        }

        public SearchRequest build()
        {
            return new SearchRequest( this );
        }
    }
}
