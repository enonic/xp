package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class IndexItemOrderBy
    extends IndexItem<IndexValueString>
{
    public IndexItemOrderBy( final IndexPath indexPath, final String value )
    {
        super( indexPath, IndexValue.create( value ) );
    }

    @Override
    public IndexValueType valueType()
    {
        return IndexValueType.ORDERBY;
    }
}
