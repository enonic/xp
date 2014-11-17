package com.enonic.wem.repo.internal.elasticsearch;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.repo.internal.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.wem.repo.internal.index.IndexFieldNameNormalizer;

public class FieldNameResolver
{
    private final static String SEPARATOR = ".";

    public static String resolve( final AbstractStoreDocumentItem item )
    {
        Preconditions.checkNotNull( item, "item could not be null" );

        if ( Strings.isNullOrEmpty( item.getPath() ) )
        {
            throw new IllegalArgumentException( "item path cannot be null or empty" );
        }

        final String normalizedName = IndexFieldNameNormalizer.normalize( item.getPath() );

        final String postFix =
            Strings.isNullOrEmpty( item.getIndexBaseType().getPostfix() ) ? "" : SEPARATOR + item.getIndexBaseType().getPostfix();

        return normalizedName + postFix;
    }

}
