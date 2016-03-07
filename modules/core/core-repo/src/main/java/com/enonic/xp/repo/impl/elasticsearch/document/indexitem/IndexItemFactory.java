package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.IndexValueProcessor;

public class IndexItemFactory
{
    public static List<IndexItem> create( final Property property, final IndexConfig indexConfig )
    {
        Value processedPropertyValue = applyValueProcessors( property.getValue(), indexConfig );

        return createItems( IndexPath.from( property ), indexConfig, processedPropertyValue );
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

    private static List<IndexItem> createItems( final IndexPath indexPath, final IndexConfig indexConfig,
                                                final Value processedPropertyValue )
    {
        final List<IndexItem> items = Lists.newArrayList();

        if ( indexConfig.isEnabled() )
        {
            items.addAll( BaseTypeFactory.create( indexPath, processedPropertyValue ) );
            items.addAll( FulltextTypeFactory.create( indexPath, processedPropertyValue, indexConfig ) );
            items.add( OrderByTypeFactory.create( indexPath, processedPropertyValue ) );
            items.addAll( AllTextTypeFactory.create( processedPropertyValue, indexConfig ) );
        }

        return items;
    }
}