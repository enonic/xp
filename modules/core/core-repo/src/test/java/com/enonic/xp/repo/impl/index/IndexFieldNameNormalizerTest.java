package com.enonic.xp.repo.impl.index;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexFieldNameNormalizerTest
{
    @Test
    void normalize_collection()
    {
        final String[] normalizedPaths = IndexFieldNameNormalizer.normalize( List.of("VaLue1", "ValuE2", "VALue3") );

        for ( final String path : normalizedPaths )
        {
            assertTrue( path.equals( "value1" ) || path.equals( "value2" ) || path.equals( "value3" ) );
        }
    }
}
