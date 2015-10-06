package com.enonic.xp.repo.impl.index;

public enum IndexValueType
{

    DATETIME( "_datetime" ),

    NUMBER( "_number" ),

    LONG( "_long" ),

    NGRAM( "_ngram" ),

    ANALYZED( "_analyzed" ),

    ORDERBY( "_orderby" ),

    GEO_POINT( "_geopoint" ),

    STRING( "" ),

    NODE( "" );

    public static final String INDEX_VALUE_TYPE_SEPARATOR = ".";

    private final String postfix;

    private IndexValueType( final String postfix )
    {
        this.postfix = postfix;
    }

    public String getPostfix()
    {
        return postfix;
    }
}
