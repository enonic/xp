package com.enonic.wem.repo.internal.storage;

public class GetByIdRequest
    extends AbstractGetRequest
{
    private final String id;

    private GetByIdRequest( final Builder builder )
    {
        super( builder );
        id = builder.id;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getId()
    {
        return id;
    }

    public static final class Builder
        extends AbstractGetRequest.Builder<Builder>
    {
        private String id;

        private Builder()
        {
        }

        public Builder id( String id )
        {
            this.id = id;
            return this;
        }

        public GetByIdRequest build()
        {
            return new GetByIdRequest( this );
        }
    }
}
