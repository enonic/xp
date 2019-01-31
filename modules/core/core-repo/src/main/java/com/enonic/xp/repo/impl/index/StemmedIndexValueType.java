package com.enonic.xp.repo.impl.index;

public class StemmedIndexValueType
    implements IndexValueTypeInterface
{

    public static final String STEMMED_INDEX_PREFIX = "_stemmed_";

    private String value;

    public StemmedIndexValueType( final String value )
    {
        this.value = value.startsWith( STEMMED_INDEX_PREFIX ) ? value : STEMMED_INDEX_PREFIX + value;
    }

    @Override
    public String getPostfix()
    {
        return value;
    }
}
