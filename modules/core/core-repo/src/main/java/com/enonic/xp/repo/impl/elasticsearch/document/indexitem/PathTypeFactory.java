package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexPath;

public class PathTypeFactory
{
    static IndexItem create( final IndexPath indexPath, final Value propertyValue )
    {
        return new IndexItemPath( indexPath, propertyValue.asString() );
    }

}
