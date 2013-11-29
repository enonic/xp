package com.enonic.wem.core.index.document;

import com.enonic.wem.core.index.IndexValueType;

public class IndexDocumentStringItem
    extends AbstractIndexDocumentItem<String>
{
    private final String value;

    public IndexDocumentStringItem( final IndexDocumentItemPath path, final String value )
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
