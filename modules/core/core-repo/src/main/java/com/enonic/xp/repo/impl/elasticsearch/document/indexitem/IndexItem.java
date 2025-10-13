package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueTypeInterface;

import static com.google.common.base.Strings.isNullOrEmpty;

public abstract class IndexItem<T extends IndexValue>
{
    public static final String INDEX_VALUE_TYPE_SEPARATOR = ".";

    private final IndexPath indexPath;

    private final T value;

    IndexItem( final IndexPath indexPath, final T value )
    {
        this.indexPath = indexPath;
        this.value = value;
    }

    private String getBasePath()
    {
        return indexPath.getPath();
    }

    public abstract IndexValueTypeInterface valueType();

    public T getValue()
    {
        return value;
    }

    public String getPath()
    {
        return getBasePath() + getTypeContextPostfix();
    }

    private String getTypeContextPostfix()
    {
        return isNullOrEmpty( valueType().getPostfix() ) ? "" : INDEX_VALUE_TYPE_SEPARATOR + valueType().getPostfix();

    }

}
