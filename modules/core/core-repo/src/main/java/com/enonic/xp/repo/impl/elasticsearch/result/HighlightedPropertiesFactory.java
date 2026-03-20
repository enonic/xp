package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Map;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.highlight.HighlightField;

import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.highlight.HighlightedProperty;
import com.enonic.xp.repo.impl.elasticsearch.highlight.ElasticHighlightQueryBuilderFactory;

public class HighlightedPropertiesFactory
{
    public static HighlightedProperties create( final Map<String, HighlightField> highlightPropertyMap )
    {
        if ( highlightPropertyMap == null )
        {
            return null;
        }

        final HighlightedProperties.Builder result = HighlightedProperties.create();

        for ( final Map.Entry<String, HighlightField> propertyEntry : highlightPropertyMap.entrySet() )
        {
            final String propertyName = removePostfix( propertyEntry.getKey() );
            final HighlightedProperty.Builder builder = HighlightedProperty.create().name( propertyName );
            for ( Text fragment : propertyEntry.getValue().fragments() )
            {
                builder.addFragment( fragment.string() );
            }
            result.add( builder.build() );
        }

        return result.build();
    }

    private static String removePostfix( final String propertyName )
    {
        for ( final String postfix : ElasticHighlightQueryBuilderFactory.POSTFIXES )
        {
            if ( propertyName.endsWith( postfix ) )
            {
                return propertyName.substring( 0, propertyName.length() - postfix.length() );
            }
        }
        return propertyName;
    }
}
