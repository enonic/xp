package com.enonic.wem.core.elasticsearch.document;

import com.enonic.wem.core.entity.index.IndexDocumentItemPath;
import com.enonic.wem.core.index.IndexValueType;

public class IndexDocumentGeoPointItem
    extends AbstractIndexDocumentItem<String>
{
    private final String value;

    public IndexDocumentGeoPointItem( final IndexDocumentItemPath path, final String value )
    {
        super( path );
        this.value = value;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.GEO_POINT;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}
