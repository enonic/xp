package com.enonic.xp.repo.impl.index.document;

import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItemAnalyzed
    extends IndexItem<IndexValueString>
{
    public IndexItemAnalyzed( final String keyBase, final String value )
    {
        super( keyBase, IndexValue.create( value ) );
    }

    @Override
    public IndexValueType valueType()
    {
        return IndexValueType.ANALYZED;
    }
}
