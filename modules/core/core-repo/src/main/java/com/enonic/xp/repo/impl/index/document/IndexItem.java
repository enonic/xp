package com.enonic.xp.repo.impl.index.document;

import com.google.common.base.Strings;

import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
import com.enonic.xp.repo.impl.index.IndexValueType;

public abstract class IndexItem<T extends IndexValue>
{
    protected final static String INDEX_VALUE_TYPE_SEPARATOR = ".";

    private String keyBase;

    private T value;

    public IndexItem( final String keyBase, final T value )
    {
        this.keyBase = keyBase;
        this.value = value;
    }

    protected String getKeyBase()
    {
        return IndexFieldNameNormalizer.normalize( keyBase );
    }

    public abstract IndexValueType valueType();

    public T getValue()
    {
        return value;
    }

    public String getKey()
    {
        return getKeyBase() + getTypeContextPostfix();
    }

    protected String getTypeContextPostfix()
    {
        return Strings.isNullOrEmpty( valueType().getPostfix() ) ? "" : INDEX_VALUE_TYPE_SEPARATOR + valueType().getPostfix();

    }

}
