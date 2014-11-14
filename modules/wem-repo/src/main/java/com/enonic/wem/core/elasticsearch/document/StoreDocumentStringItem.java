package com.enonic.wem.core.elasticsearch.document;

import com.enonic.wem.api.index.IndexDocumentItemPath;
import com.enonic.wem.core.index.IndexValueType;

public class StoreDocumentStringItem
    extends AbstractStoreDocumentItem<String>
{
    private final String value;

    public StoreDocumentStringItem( final IndexDocumentItemPath path, final String value )
    {
        super( path );
        this.value = value;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.STRING;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}
