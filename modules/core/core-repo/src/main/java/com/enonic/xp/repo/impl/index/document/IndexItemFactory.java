package com.enonic.xp.repo.impl.index.document;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessor;

public class IndexItemFactory
{
    public static List<IndexItem> create( final Property property, final IndexConfig indexConfig )
    {
        return doCreate( property.getName(), property.getValue(), indexConfig );
    }

    public static List<IndexItem> create( final String name, final Value value, final IndexConfig indexConfig )
    {
        return doCreate( name, value, indexConfig );
    }

    private static List<IndexItem> doCreate( final String name, final Value value, final IndexConfig indexConfig )
    {
        Value processedPropertyValue = applyValueProcessors( value, indexConfig );

        return createItems( name, indexConfig, processedPropertyValue );
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

    private static List<IndexItem> createItems( final String name, final IndexConfig indexConfig, final Value processedPropertyValue )
    {
        final List<IndexItem> items = Lists.newArrayList();

        items.addAll( BaseTypeFactory.create( name, processedPropertyValue ) );
        items.addAll( FulltextTypeFactory.create( name, processedPropertyValue, indexConfig ) );
        items.add( OrderByTypeFactory.create( name, processedPropertyValue ) );
        items.addAll( AllTextTypeFactory.create( processedPropertyValue, indexConfig ) );

        return items;
    }


}