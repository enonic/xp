package com.enonic.wem.repo.internal.elasticsearch.document;

import com.enonic.xp.core.index.IndexPath;
import com.enonic.wem.repo.internal.index.IndexValueType;

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
