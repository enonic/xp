package com.enonic.xp.repo.impl;

import java.util.Arrays;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.support.AbstractImmutableEntitySet;

public class ReturnFields
    extends AbstractImmutableEntitySet<ReturnField>
{
    private ReturnFields( final ImmutableSet<ReturnField> set )
    {
        super( set );
    }

    public static ReturnFields empty()
    {
        return new ReturnFields( ImmutableSet.of() );
    }

    public static ReturnFields from( final IndexPath... indexPath )
    {
        return new ReturnFields( Arrays.stream( indexPath ).map( ReturnField::new ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public String[] getReturnFieldNames()
    {
        return this.set.stream().map( ReturnField::getPath ).toArray( String[]::new );
    }
}
