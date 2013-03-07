package com.enonic.wem.core.index.elastic.result;


public abstract class AbstractFacetResultSetFactory
{
    protected static Double getValueIfNumber( final double entry )
    {
        return Double.isNaN( entry ) ? null : entry;
    }
}
