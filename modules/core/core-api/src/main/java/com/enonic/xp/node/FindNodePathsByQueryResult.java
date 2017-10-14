package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public class FindNodePathsByQueryResult
{
    private final NodePaths paths;

    private FindNodePathsByQueryResult( final Builder builder )
    {
        this.paths = builder.nodePaths.build();
    }

    public NodePaths getPaths()
    {
        return paths;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final NodePaths.Builder nodePaths = NodePaths.create();

        private Builder()
        {
        }

        public Builder paths( NodePaths paths )
        {
            if ( paths != null )
            {
                nodePaths.addNodePaths( paths.getSet() );
            }
            return this;
        }

        public Builder path(NodePath path) {
            if(path != null) {
                nodePaths.addNodePath( path );
            }
            return this;
        }

        public Builder path(String path) {
            if(path != null) {
                nodePaths.addNodePath( NodePath.create( path ).build() );
            }
            return this;
        }

        public FindNodePathsByQueryResult build()
        {
            return new FindNodePathsByQueryResult( this );
        }
    }
}
