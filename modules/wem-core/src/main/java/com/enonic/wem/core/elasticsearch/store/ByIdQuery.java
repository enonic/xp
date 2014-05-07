package com.enonic.wem.core.elasticsearch.store;

public class ByIdQuery
    extends AbstractByQuery
{
    private final String id;

    @Override
    public int size()
    {
        return 1;
    }

    private ByIdQuery( final Builder builder )
    {
        super( builder );
        this.id = builder.id;
    }

    public String getId()
    {
        return id;
    }

    public static Builder byId( final String id )
    {
        return new Builder( id );
    }

    public static class Builder
        extends AbstractByQuery.Builder<Builder>
    {
        private String id;

        public Builder( final String id )
        {
            this.id = id;
        }

        public ByIdQuery build()
        {
            return new ByIdQuery( this );
        }
    }

}
