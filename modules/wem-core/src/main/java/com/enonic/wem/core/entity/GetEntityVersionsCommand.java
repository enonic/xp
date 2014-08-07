package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityVersions;
import com.enonic.wem.core.version.GetVersionsQuery;
import com.enonic.wem.core.version.VersionService;

public class GetEntityVersionsCommand
{
    private final EntityId entityId;

    private final Context context;

    private final int from;

    private final int size;

    private final VersionService versionService;

    private GetEntityVersionsCommand( Builder builder )
    {
        entityId = builder.entityId;
        context = builder.context;
        from = builder.from;
        size = builder.size;
        versionService = builder.versionService;
    }

    public EntityVersions execute()
    {
        final GetVersionsQuery query = GetVersionsQuery.create().
            entityId( this.entityId ).
            from( this.from ).
            size( this.size ).
            build();

        return this.versionService.getVersions( query );
    }

    public static Builder create( final Context context )
    {
        return new Builder( context );
    }

    public static final class Builder
    {
        private EntityId entityId;

        private Context context;

        private int from;

        private int size;

        private VersionService versionService;

        private Builder( final Context context )
        {
            this.context = context;
        }

        public Builder entityId( EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public Builder versionService( VersionService versionService )
        {
            this.versionService = versionService;
            return this;
        }

        public GetEntityVersionsCommand build()
        {
            return new GetEntityVersionsCommand( this );
        }
    }
}
