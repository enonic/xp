package com.enonic.wem.core.index.elastic;

public class ByPathQuery
    extends AbstractByQuery
{

    private final String path;

    @Override
    public int size()
    {
        return 1;
    }

    private ByPathQuery( final Builder builder )
    {
        super( builder );
        this.path = builder.path;
    }

    public String getPath()
    {
        return path;
    }

    public static Builder byPath( final String parentPath )
    {
        return new Builder( parentPath );
    }

    public static class Builder
        extends AbstractByQuery.Builder<Builder>
    {
        private String path;

        public Builder( final String path )
        {
            this.path = path;
        }

        public ByPathQuery build()
        {
            return new ByPathQuery( this );
        }

    }

}
