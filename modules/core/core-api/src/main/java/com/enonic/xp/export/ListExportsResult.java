package com.enonic.xp.export;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class ListExportsResult
    extends AbstractImmutableEntityList<ExportInfo>
{
    private ListExportsResult( final ImmutableList<ExportInfo> list )
    {
        super( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ExportInfo> exports = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder addExport( final ExportInfo export )
        {
            this.exports.add( export );
            return this;
        }

        public ListExportsResult build()
        {
            return new ListExportsResult( exports.build() );
        }
    }
}
