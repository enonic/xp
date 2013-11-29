package com.enonic.wem.core.index.document;

import com.enonic.wem.core.index.IndexValueType;

public class IndexDocumentNumberItem
    extends AbstractIndexDocumentItem<Double>
{
    private final Double value;

    public IndexDocumentNumberItem( final IndexDocumentItemPath path, final Double value )
    {
        super( path );
        this.value = value;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.NUMBER;
    }

    @Override
    public Double getValue()
    {
        return value;
    }
}
