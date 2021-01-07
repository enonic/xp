package com.enonic.xp.repo.impl;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.support.AbstractImmutableEntitySet;

public class ReturnFields
    extends AbstractImmutableEntitySet<ReturnField>
{
    private ReturnFields( final Builder builder )
    {
        super( builder.set.build() );
    }

    public static ReturnFields empty()
    {
        return create().build();
    }

    public static ReturnFields from( final IndexPath... indexPath )
    {
        final ReturnFields.Builder result = ReturnFields.create();

        Arrays.stream( indexPath ).
            forEach( result::add );

        return result.build();
    }

    public String[] getReturnFieldNames()
    {
        return this.set.stream().map( ReturnField::getPath ).toArray( String[]::new );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<ReturnField> set = ImmutableSet.builder();

        public Builder add( final IndexPath indexPath )
        {
            this.set.add( new ReturnField( indexPath ) );
            return this;
        }

        public Builder addAll( final Collection<ReturnField> returnFields )
        {
            this.set.addAll( returnFields );
            return this;
        }

        public ReturnFields build()
        {
            return new ReturnFields( this );
        }
    }
}
