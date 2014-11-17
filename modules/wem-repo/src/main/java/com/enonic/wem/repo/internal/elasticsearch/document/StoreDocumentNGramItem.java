package com.enonic.wem.repo.internal.elasticsearch.document;

import com.enonic.wem.api.index.IndexDocumentItemPath;
import com.enonic.wem.core.index.IndexValueType;

public class StoreDocumentNGramItem
    extends AbstractStoreDocumentItem<String>
{
    private final String value;

    public StoreDocumentNGramItem( final IndexDocumentItemPath path, final String value )
    {
        super( path );
        this.value = value;
    }

    @Override
    public IndexValueType getIndexBaseType()
    {
        return IndexValueType.NGRAM;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}
