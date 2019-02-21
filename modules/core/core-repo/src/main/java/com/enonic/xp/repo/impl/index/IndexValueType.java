package com.enonic.xp.repo.impl.index;

public enum IndexValueType
    implements IndexValueTypeInterface
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

    private IndexValueType( final String postfix )
    {
        this.postfix = postfix;
    }

    public String getPostfix()
    {
        return postfix;
    }
}
