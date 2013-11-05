package com.enonic.wem.core.index.document;

public enum IndexValueType
{

    DATETIME( "_datetime" ),

    NUMBER( "_number" ),

    TOKENIZED( "_tokenized" ),

    ANALYZED( "_analyzed" ),

    SORTABLE( "_orderby" ),

    GEO_POINT( "_geopoint" ),

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
