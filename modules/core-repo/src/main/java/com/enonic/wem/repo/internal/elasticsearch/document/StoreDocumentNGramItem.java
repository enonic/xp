package com.enonic.wem.repo.internal.elasticsearch.document;

import com.enonic.xp.core.index.IndexPath;
import com.enonic.wem.repo.internal.index.IndexValueType;

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
