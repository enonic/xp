package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import com.google.common.base.Strings;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
import com.enonic.xp.repo.impl.index.IndexValueType;

public abstract class IndexItem<T extends IndexValue>
{
    public final static String INDEX_VALUE_TYPE_SEPARATOR = ".";

    private IndexPath indexPath;

    private T value;

    public IndexItem( final IndexPath indexPath, final T value )
    {
        this.indexPath = indexPath;
        this.value = value;
    }

    protected String getBasePath()
    {
        return IndexFieldNameNormalizer.normalize( indexPath.getPath() );
    }

    public abstract IndexValueType valueType();

    public T getValue()
    {
        return value;
    }

    public String getPath()
    {
        return getBasePath() + getTypeContextPostfix();
    }

    protected String getTypeContextPostfix()
    {
        return Strings.isNullOrEmpty( valueType().getPostfix() ) ? "" : INDEX_VALUE_TYPE_SEPARATOR + valueType().getPostfix();

    }

}
