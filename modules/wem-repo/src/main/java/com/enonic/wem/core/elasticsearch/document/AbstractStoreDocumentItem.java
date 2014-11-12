package com.enonic.wem.core.elasticsearch.document;

import com.enonic.wem.api.index.IndexDocumentItemPath;
import com.enonic.wem.core.index.IndexValueType;

public abstract class AbstractStoreDocumentItem<T>
{

    private final IndexDocumentItemPath indexDocumentItemPath;

    AbstractStoreDocumentItem( final IndexDocumentItemPath path )
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
