package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.ArrayList;
import java.util.List;

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
        final IndexPath indexPath = IndexPath.from( property.getPath() );
        final Value processedPropertyValue =
            applyValueProcessors( property.getValue(), indexConfigDocument.getConfigForPath( indexPath ) );

        return createItems( indexPath, indexConfigDocument, processedPropertyValue );
    }

    public static List<IndexItem> create( final IndexPath indexPath, final Value value, final IndexConfigDocument indexConfigDocument )
    {
        return doCreate( indexPath, value, indexConfigDocument );
    }

    private static List<IndexItem> doCreate( final IndexPath indexPath, final Value value, final IndexConfigDocument indexConfigDocument )
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
        final List<IndexItem> items = new ArrayList<>();

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

        return items;
    }
}