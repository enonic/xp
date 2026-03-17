package com.enonic.xp.repo.impl.index;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum StaticIndexValueType
    implements IndexValueType
{
    DATETIME( "_datetime" ),

    NUMBER( "_number" ),

    NGRAM( "_ngram" ),

    ANALYZED( "_analyzed" ),

    ORDERBY( "_orderby" ),

    GEO_POINT( "_geopoint" ),

    PATH( "_path" ),

    STRING( "" );

    private final String postfix;

    StaticIndexValueType( final String postfix )
    {
        this.postfix = postfix;
    }

    @Override
    public String getPostfix()
    {
        return postfix;
    }
}
