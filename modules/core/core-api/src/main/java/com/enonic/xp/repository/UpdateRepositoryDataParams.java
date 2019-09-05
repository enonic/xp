package com.enonic.xp.repository;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.security.acl.AccessControlList;
import com.google.common.base.Preconditions;

import java.util.Objects;
import java.util.Optional;

public final class UpdateRepositoryDataParams
{
    private final RepositoryData data;

    private UpdateRepositoryDataParams( final Builder builder )
    {
        this.data = Optional.ofNullable( builder.data ).orElse( RepositoryData.create( new PropertyTree() ) );
    }

    public RepositoryData getData()
    {
        return data;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private RepositoryData data;

        private Builder()
        {
        }


        public Builder data( final RepositoryData data )
        {
            this.data = data;
            return this;
        }

        public UpdateRepositoryDataParams build()
        {
            return new UpdateRepositoryDataParams( this );
        }
    }
}
