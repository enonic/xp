package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueTypeInterface;
import com.enonic.xp.repo.impl.index.OrderByIndexValueType;

class IndexItemOrderByLanguage
    extends IndexItem<IndexValueString>
{
    private final OrderByIndexValueType valueType;

    IndexItemOrderByLanguage( final IndexPath indexPath, final String value, final String language )
    {
        super( indexPath, IndexValue.create( value ) );
        this.valueType = new OrderByIndexValueType( language );
    }

    @Override
    public IndexValueTypeInterface valueType()
    {
        return valueType;
    }
}
