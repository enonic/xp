package com.enonic.wem.core.elasticsearch.document;

import com.enonic.wem.core.entity.index.IndexDocumentItemPath;
import com.enonic.wem.core.index.IndexValueType;

class IndexDocumentNGramItem
    extends AbstractIndexDocumentItem<String>
{
    private final String value;

    public IndexDocumentNGramItem( final IndexDocumentItemPath path, final String value )
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
