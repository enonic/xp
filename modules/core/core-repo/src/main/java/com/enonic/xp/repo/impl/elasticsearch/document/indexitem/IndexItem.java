package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexValueTypeInterface;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class IndexItem<T>
{
    public static final String INDEX_VALUE_TYPE_SEPARATOR = ".";

    private final IndexPath indexPath;

    private final T value;

    private final IndexValueTypeInterface valueType;

    public IndexItem( final IndexPath indexPath, final T value, final IndexValueTypeInterface valueType )
    {
        this.indexPath = indexPath;
        this.value = value;
        this.valueType = valueType;
    }

    public IndexValueTypeInterface valueType()
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
        return isNullOrEmpty( valueType().getPostfix() ) ? "" : INDEX_VALUE_TYPE_SEPARATOR + valueType().getPostfix();
    }
}
