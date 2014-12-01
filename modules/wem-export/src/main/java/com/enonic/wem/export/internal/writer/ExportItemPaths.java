package com.enonic.wem.export.internal.writer;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class ExportItemPaths
    extends AbstractImmutableEntitySet<ExportItemPath>
{

    public ExportItemPaths( final ImmutableSet<ExportItemPath> set )
    {
        super( set );
    }

    private ExportItemPaths( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.exportItemPaths ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {

        private Builder()
        {
        }

        private final Set<ExportItemPath> exportItemPaths = Sets.newHashSet();

        public Builder add( final ExportItemPath exportItemPath )
        {
            this.exportItemPaths.add( exportItemPath );
            return this;
        }

        public ExportItemPaths build()
        {
            return new ExportItemPaths( this );
        }

    }


}
