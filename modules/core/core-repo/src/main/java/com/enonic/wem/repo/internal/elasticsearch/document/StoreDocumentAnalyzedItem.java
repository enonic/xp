package com.enonic.wem.repo.internal.elasticsearch.document;

import com.enonic.wem.repo.internal.index.IndexValueType;
import com.enonic.xp.index.IndexPath;

public class StoreDocumentAnalyzedItem
    extends AbstractStoreDocumentItem<String>
{

    private final String value;

    public StoreDocumentAnalyzedItem( final IndexPath path, final String value )
    {
        super( path );
        this.value = value;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.ANALYZED;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}
