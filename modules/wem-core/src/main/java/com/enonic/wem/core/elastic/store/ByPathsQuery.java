package com.enonic.wem.core.elastic.store;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;

public class ByPathsQuery
    extends AbstractByQuery
{

    private final ImmutableSet<String> paths;


    @Override
    public int size()
    {
        return 1;
    }

    private ByPathsQuery( final Builder builder )
    {
        super( builder );
        this.paths = ImmutableSet.copyOf( builder.paths );
    }

    public ImmutableSet<String> getPaths()
    {
        return paths;
    }

    public static Builder byPaths()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractByQuery.Builder<Builder>
    {
        private final Set<String> paths = Sets.newHashSet();

        public Builder setPaths( final NodePaths nodePaths )
        {
            if ( nodePaths == null )
            {
                return this;
            }

            for ( final NodePath nodePath : nodePaths )
            {
                paths.add( nodePath.toString() );
            }

            return this;
        }

        public ByPathsQuery build()
        {
            return new ByPathsQuery( this );
        }

    }

}

