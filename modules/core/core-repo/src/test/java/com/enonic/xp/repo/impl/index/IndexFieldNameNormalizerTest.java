package com.enonic.xp.repo.impl.index;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndexFieldNameNormalizerTest
{
    @Test
    void normalize()
    {
        assertEquals( "value1", IndexFieldNameNormalizer.normalize( "VaLue1" ) );
        assertEquals( "value2", IndexFieldNameNormalizer.normalize( "ValuE2" ) );
        assertEquals( "value3", IndexFieldNameNormalizer.normalize( "VALue3" ) );
    }
}
