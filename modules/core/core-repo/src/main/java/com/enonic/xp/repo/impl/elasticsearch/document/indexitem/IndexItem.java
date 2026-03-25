package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

public final class IndexItem<T>
{
    public static final String INDEX_VALUE_TYPE_SEPARATOR = ".";

    private final IndexPath indexPath;

    private final T value;

    private final IndexValueType valueType;

    public IndexItem( final IndexPath indexPath, final T value, final IndexValueType valueType )
    {
        this.indexPath = indexPath;
        this.value = value;
        this.valueType = valueType;
    }

    public IndexValueType valueType()
    {
        return valueType;
    }

    public T getValue()
    {
        return value;
    }

    public String getPath()
    {
        return indexPath.getPath() + getTypeContextPostfix();
    }

    private String getTypeContextPostfix()
    {
        return valueType().getPostfix().isEmpty() ? "" : INDEX_VALUE_TYPE_SEPARATOR + valueType().getPostfix();
    }
}
