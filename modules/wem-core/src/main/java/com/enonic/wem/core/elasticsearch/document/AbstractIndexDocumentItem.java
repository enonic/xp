package com.enonic.wem.core.elasticsearch.document;

import com.enonic.wem.core.entity.index.IndexDocumentItemPath;
import com.enonic.wem.core.index.IndexValueType;

public abstract class AbstractIndexDocumentItem<T>
{

    private final IndexDocumentItemPath indexDocumentItemPath;

    AbstractIndexDocumentItem( final IndexDocumentItemPath path )
    {
        this.indexDocumentItemPath = path;
    }

    public String getPath()
    {
        return indexDocumentItemPath.toString();
    }

    public abstract IndexValueType getIndexBaseType();

    public abstract T getValue();

}
