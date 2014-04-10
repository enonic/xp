package com.enonic.wem.core.index.document;

import com.enonic.wem.core.index.IndexValueType;

public class IndexDocumentNodeItem
    extends AbstractIndexDocumentItem<String>
{
    private String nodeAsJsonString;

    public IndexDocumentNodeItem( final IndexDocumentItemPath path, final String nodeAsJsonString )
    {
        super( path );
        this.nodeAsJsonString = nodeAsJsonString;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.NODE;
    }

    @Override
    public String getValue()
    {
        return nodeAsJsonString;
    }
}
