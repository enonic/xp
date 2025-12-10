package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.IndexValueProcessor;

class IndexItemFactory
{
    public static List<IndexItem> create( final IndexPath indexPath, final Value value, final IndexConfigDocument indexConfigDocument )
    {
        Value processedPropertyValue = applyValueProcessors( value, indexConfigDocument.getConfigForPath( indexPath ) );

        return createItems( indexPath, indexConfigDocument, processedPropertyValue );
    }

    private static Value applyValueProcessors( final Value value, final IndexConfig indexConfig )
    {
        Value processedPropertyValue = value;

        for ( IndexValueProcessor indexValueProcessor : indexConfig.getIndexValueProcessors() )
        {
            processedPropertyValue = indexValueProcessor.process( processedPropertyValue );
        }
        return processedPropertyValue;
    }

    private static List<IndexItem> createItems( final IndexPath indexPath, final IndexConfigDocument indexConfigDocument,
                                                final Value processedPropertyValue )
    {
        final ImmutableList.Builder<IndexItem> items = ImmutableList.builder();

        final IndexConfig indexConfig = indexConfigDocument.getConfigForPath( indexPath );

        if ( indexConfig.isEnabled() )
        {
            items.addAll( BaseTypeFactory.create( indexPath, processedPropertyValue ) );
            items.addAll( FulltextTypeFactory.create( indexPath, processedPropertyValue, indexConfig ) );
            items.add( OrderByTypeFactory.create( indexPath, processedPropertyValue ) );
            items.addAll( AllTextTypeFactory.create( processedPropertyValue, indexConfig, indexConfigDocument.getAllTextConfig() ) );
            items.addAll( StemmedTypeFactory.create( indexPath, processedPropertyValue, indexConfig ) );

            if ( indexConfig.isPath() )
            {
                items.add( PathTypeFactory.create( indexPath, processedPropertyValue ) );
            }
        }

        return items.build();
    }
}
