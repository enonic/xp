package com.enonic.wem.core.index.query

import com.enonic.wem.core.index.IndexFieldNameNormalizer
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class IndexFieldNameNormalizerTest extends Specification
{

    def "normalize #path to #normalizedFieldName"( )
    {
        expect:
        normalizedFieldName == IndexFieldNameNormalizer.normalize( path )

        where:

        path    | normalizedFieldName
        "A"     | "a"
        "a"     | "a"
        "a.b"   | "a_b"
        "a_b.c" | "a_b_c"
        "a.b.c" | "a_b_c"
    }


}
