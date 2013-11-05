package com.enonic.wem.core.index.document;

public class IndexDocumentNumberItem
    extends AbstractIndexDocumentItem<Double>
{
    private final Double value;

    public IndexDocumentNumberItem( final String fieldName, final Double value )
    {
        super( fieldName );
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
