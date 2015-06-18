package com.enonic.wem.repo.internal.index;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import static org.junit.Assert.*;

public class IndexFieldNameNormalizerTest
{
    @Test
    public void normalize_collection()
        throws Exception
    {
        Set<String> paths = Sets.newHashSet();
        paths.add( "VaLue1" );
        paths.add( "ValuE2" );
        paths.add( "VALue3" );

        final String[] normalizedPaths = IndexFieldNameNormalizer.normalize( paths );

        for ( final String path : normalizedPaths )
        {
            assertTrue( path.equals( "value1" ) || path.equals( "value2" ) || path.equals( "value3" ) );
        }
    }
}