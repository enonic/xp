package com.enonic.wem.repo.internal.elasticsearch.document;

public class DeleteDocument
    extends AbstractIndexDocument
{
    private final String id;

    private DeleteDocument( final Builder builder )
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


    public static class Builder
        extends AbstractIndexDocument.Builder<Builder>
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

        public DeleteDocument build()
        {
            return new DeleteDocument( this );
        }
    }
}
