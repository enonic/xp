package com.enonic.wem.api.converter

import spock.lang.Specification

class ConverterRegistryTest
    extends Specification
{
    def "unknown converter"()
    {
        given:
        def registry = new ConverterRegistry()
        def converter = registry.find( String.class, Double.class )

        expect:
        converter == null
    }
}
