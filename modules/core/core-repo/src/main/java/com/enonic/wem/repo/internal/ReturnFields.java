package com.enonic.wem.repo.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.support.AbstractImmutableEntitySet;

public class ReturnFields
    extends AbstractImmutableEntitySet<ReturnField>
{
    private ReturnFields( final Set<ReturnField> set )
    {
        super( ImmutableSet.copyOf( set ) );
    }

    public static ReturnFields empty()
    {
        final Set<ReturnField> returnFields = Sets.newHashSet();
        return new ReturnFields( returnFields );
    }

    public static ReturnFields from( final IndexPath... indexPath )
    {
        return ReturnFields.fromCollection( Collections2.transform( Arrays.asList( indexPath ), ReturnField::new ) );
    }

    private static ReturnFields fromCollection( final Collection<ReturnField> returnFields )
    {
        return new ReturnFields( ImmutableSet.copyOf( returnFields ) );
    }

    public String[] getReturnFieldNames()
    {
        return Collections2.transform( this.set, ReturnField::getPath ).toArray( new String[this.set.size()] );
    }

}
