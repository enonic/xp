package com.enonic.wem.core.elasticsearch.query.builder.function

import com.enonic.wem.core.index.function.WeightedQueryFieldName
import spock.lang.Specification
import spock.lang.Unroll

class WeightedQueryFieldNameTest
    extends Specification
{

    @Unroll
    def "create WeightedQueryFieldName from value '#stringValue'"()
    {
        expect:
        WeightedQueryFieldName weightedQueryFieldName = WeightedQueryFieldName.from( stringValue )
        weightedQueryFieldName.getBaseFieldName() == baseFieldName
        weightedQueryFieldName.getWeight() == weight

        where:
        stringValue | baseFieldName | weight
        "myField"   | "myField"     | null
        "myField^5" | "myField"     | 5
        "myField^"  | "myField"     | null
        "^5"        | ""            | 5
    }


}
