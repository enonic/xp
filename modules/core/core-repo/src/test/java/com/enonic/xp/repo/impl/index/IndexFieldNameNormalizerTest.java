package com.enonic.xp.repo.impl.index;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IndexFieldNameNormalizerTest
{
    @Test
    public void normalize_collection()
        throws Exception
    {
        Set<String> paths = new HashSet<>();
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
