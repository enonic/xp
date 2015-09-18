package com.enonic.wem.repo.internal.elasticsearch.document;

import com.enonic.xp.index.IndexPath;
import com.enonic.wem.repo.internal.index.IndexValueType;

public class StoreDocumentNumberItem
    extends AbstractStoreDocumentItem<Double>
{
    private final Double value;

    public StoreDocumentNumberItem( final IndexPath path, final Double value )
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
