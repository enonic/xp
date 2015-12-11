package com.enonic.xp.repo.impl.index.document;

import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItemNgram
    extends IndexItem<IndexValueString>
{

    public IndexItemNgram( final String keyBase, final String value )
    {
        super( keyBase, IndexValue.create( value ) );
    }

    @Override
    public IndexValueType valueType()
    {
        return IndexValueType.NGRAM;
    }
}
