package com.enonic.wem.core.index.elastic;

public class ByParentPathQuery
    extends AbstractByQuery
{
    private final String parentPath;

    @Override
    public int size()
    {
        return DEFAULT_MAX_SIZE;
    }

    private ByParentPathQuery( final Builder builder )
    {
        super( builder );
        this.parentPath = builder.parentPath;
    }

    public String getPath()
    {
        return parentPath;
    }

    public static Builder byParentPath( final String parentPath )
    {
        return new Builder( parentPath );
    }

    public static class Builder
        extends AbstractByQuery.Builder<Builder>
    {
        private String parentPath;

        public Builder( final String path )
        {
            this.parentPath = path;
        }

        public ByParentPathQuery build()
        {
            return new ByParentPathQuery( this );
        }

    }
}
