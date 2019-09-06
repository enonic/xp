package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.highlight.HighlightField;

import com.enonic.xp.highlight.HighlightedProperty;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class HighlightedPropertiesFactory
{
    private static final Pattern POSTFIX_PATTERN = Pattern.compile(
        "\\.(?:" + IndexValueType.DATETIME.getPostfix() + "|" + IndexValueType.NUMBER.getPostfix() + "|" +
            IndexValueType.NGRAM.getPostfix() + "|" + IndexValueType.ANALYZED.getPostfix() + "|" + IndexValueType.ORDERBY.getPostfix() +
            "|" + IndexValueType.GEO_POINT.getPostfix() + "|" + IndexValueType.PATH.getPostfix() + ")$" );

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
            final HighlightedProperty.Builder builder = HighlightedProperty.create().
                name( propertyName );
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
        final Matcher matcher = POSTFIX_PATTERN.matcher( propertyName );
        if ( matcher.find() )
        {
            return propertyName.substring( 0, matcher.start() );
        }
        return propertyName;
    }
}
