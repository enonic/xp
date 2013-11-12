package com.enonic.wem.core.index.document;

import com.enonic.wem.api.data.Property;

public abstract class AbstractIndexDocumentItem<T>
{

    protected IndexDocumentItemPath indexDocumentItemPath;

    protected AbstractIndexDocumentItem( final IndexDocumentItemPath path )
    {
        this.indexDocumentItemPath = path;
    }

    protected AbstractIndexDocumentItem( final Property property )
    {
        this.indexDocumentItemPath = IndexDocumentItemPath.from( property );
    }

    public String getPath()
    {
        return indexDocumentItemPath.toString();
    }

    public abstract IndexValueType getIndexBaseType();

    public abstract T getValue();

}
