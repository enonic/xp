package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.IndexValueProcessor;

class IndexItemFactory
{
    public static List<IndexItem> create( final Property property, final IndexConfigDocument indexConfigDocument )
    {
        Value processedPropertyValue = applyValueProcessors( property.getValue(), indexConfigDocument );

        return createItems( IndexPath.from( property ), indexConfigDocument, processedPropertyValue );
    }

    public static List<IndexItem> create( final String name, final Value value, final IndexConfig indexConfig )
    {
        return doCreate( name, value, indexConfig );
    }

    private static List<IndexItem> doCreate( final String name, final Value value, final IndexConfig indexConfig )
    {
        Value processedPropertyValue = applyValueProcessors( value, indexConfig );

        return createItems( IndexPath.from( name ), indexConfig, processedPropertyValue );
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
        final List<IndexItem> items = Lists.newArrayList();

        indexConfigDocument.getConfigForPath( indexPath.getPath() )

        if ( indexConfig.isEnabled() )
        {
            items.addAll( BaseTypeFactory.create( indexPath, processedPropertyValue ) );
            items.addAll( FulltextTypeFactory.create( indexPath, processedPropertyValue, indexConfig ) );
            items.add( OrderByTypeFactory.create( indexPath, processedPropertyValue ) );
            items.addAll( AllTextTypeFactory.create( processedPropertyValue, indexConfig ) );
            items.addAll( StemmedTypeFactory.create( indexPath, processedPropertyValue, indexConfig ) );

            if ( indexConfig.isPath() )
            {
                items.add( PathTypeFactory.create( indexPath, processedPropertyValue ) );
            }
        }

        return items;
    }
}