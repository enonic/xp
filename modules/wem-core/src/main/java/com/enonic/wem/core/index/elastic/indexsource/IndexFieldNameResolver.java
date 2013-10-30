package com.enonic.wem.core.index.elastic.indexsource;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.core.index.document.AbstractIndexDocumentItem;

public class IndexFieldNameResolver
{
    private final static String SEPARATOR = ".";

    public static String create( final AbstractIndexDocumentItem item )
    {
        Preconditions.checkNotNull( item, "item could not be null" );

        if ( Strings.isNullOrEmpty( item.getFieldBaseName() ) )
        {
            throw new IllegalArgumentException( "item fieldname can not be null or empty" );
        }

        return washFieldBasename( item.getFieldBaseName() + ( Strings.isNullOrEmpty( item.getIndexBaseType().getPostfix() )
            ? ""
            : SEPARATOR + item.getIndexBaseType().getPostfix() ) );

    }

    private static String washFieldBasename( final String fieldBaseName )
    {
        return fieldBaseName.toLowerCase();
    }

}
