package com.enonic.wem.core.index;

public enum IndexValueType
{

    DATETIME( "_datetime" ),

    NUMBER( "_number" ),

    TOKENIZED( "_tokenized" ),

    ANALYZED( "_analyzed" ),

    SORTABLE( "_orderby" ),

    GEO_POINT( "_geopoint" ),

    STRING( "" );

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
