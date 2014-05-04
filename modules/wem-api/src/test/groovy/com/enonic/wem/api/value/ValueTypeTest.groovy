package com.enonic.wem.api.value

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ValueTypeTest
    extends Specification
{
    def "test ValueType.#type.name()"()
    {
        expect:
        type.name == name

        where:
        type                 | name
        ValueType.BOOLEAN    | "Boolean"
        ValueType.LONG       | "Long"
        ValueType.DOUBLE     | "Double"
        ValueType.STRING     | "String"
        ValueType.CONTENT_ID | "ContentId"
        ValueType.ENTITY_ID  | "EntityId"
        ValueType.DATE       | "Date"
        ValueType.DATE_TIME  | "DateTime"
        ValueType.DATA       | "Data"
        ValueType.HTML_PART  | "HtmlPart"
        ValueType.XML        | "Xml"
    }
}
