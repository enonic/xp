package com.enonic.wem.repo.internal.elasticsearch.document;

import com.enonic.wem.repo.internal.index.IndexValueType;
import com.enonic.xp.index.IndexPath;

public class StoreDocumentNGramItem
    extends AbstractStoreDocumentItem<String>
{
    private final String value;

    public StoreDocumentNGramItem( final IndexPath path, final String value )
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
