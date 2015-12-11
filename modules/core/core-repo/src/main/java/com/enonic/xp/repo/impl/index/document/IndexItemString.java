package com.enonic.xp.repo.impl.index.document;

import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItemString
    extends IndexItem<IndexValueString>
{
    public IndexItemString( final String keyBase, final String value )
    {
        super( keyBase, IndexValue.create( value ) );
    }

    @Override
    public IndexValueType valueType()
    {
        return IndexValueType.STRING;
    }
}
