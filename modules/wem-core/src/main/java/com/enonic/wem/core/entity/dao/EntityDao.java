package com.enonic.wem.core.entity.dao;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.Entity;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIndexConfig;

public interface EntityDao
{
    public Entity create( CreateEntityArgs args );

    public void update( UpdateEntityArgs args );

    public Entity getById( EntityId id );

    public void deleteById( EntityId id );

    public class CreateEntityArgs
    {
        final RootDataSet data;

        final EntityIndexConfig entityIndexConfig;

        CreateEntityArgs( Builder builder )
        {
            this.data = builder.data;
            this.entityIndexConfig = builder.entityIndexConfig;
        }

        public static class Builder
        {
            RootDataSet data;

            EntityIndexConfig entityIndexConfig;

            public Builder data( RootDataSet data )
            {
                this.data = data;
                return this;
            }

            public Builder entityIndexConfig( EntityIndexConfig entityIndexConfig )
            {
                this.entityIndexConfig = entityIndexConfig;
                return this;
            }

            public CreateEntityArgs build()
            {
                return new CreateEntityArgs( this );
            }
        }
    }

    public class UpdateEntityArgs
    {
        final EntityId entityToUpdate;

        final RootDataSet data;

        final EntityIndexConfig entityIndexConfig;

        UpdateEntityArgs( Builder builder )
        {
            this.entityToUpdate = builder.entityToUpdate;
            this.data = builder.data;
            this.entityIndexConfig = builder.entityIndexConfig;
        }

        public static class Builder
        {
            EntityId entityToUpdate;

            RootDataSet data;

            EntityIndexConfig entityIndexConfig;

            public Builder entityToUpdate( EntityId value )
            {
                this.entityToUpdate = value;
                return this;
            }

            public Builder data( RootDataSet data )
            {
                this.data = data;
                return this;
            }

            public Builder entityIndexConfig( EntityIndexConfig entityIndexConfig )
            {
                this.entityIndexConfig = entityIndexConfig;
                return this;
            }

            public UpdateEntityArgs build()
            {
                return new UpdateEntityArgs( this );
            }
        }
    }
}


