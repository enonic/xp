package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.elasticsearch.OrderbyValueResolver;
import com.enonic.xp.repo.impl.index.IndexLanguageController;

class OrderByTypeFactory
{

    static List<IndexItem> create( final IndexPath indexPath, final Value propertyValue, final IndexConfig indexConfig )
    {
        final List<IndexItem> items = new ArrayList<>();

        items.add( new IndexItemOrderBy( indexPath, OrderbyValueResolver.getOrderbyValue( propertyValue ) ) );

        if ( !indexConfig.getLanguages().isEmpty() )
        {
            final String orderByValue = OrderbyValueResolver.getOrderbyValue( propertyValue );
            indexConfig.getLanguages().forEach( language -> {
                if ( !IndexLanguageController.isSupported( language ) )
                {
                    throw new IllegalArgumentException( "Unsupported language for sort indexing: " + language );
                }
                items.add( new IndexItemOrderByLanguage( indexPath, orderByValue, language ) );
            } );
        }
        return items;
    }
}
