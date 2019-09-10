package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.highlight.HighlightField;

import com.enonic.xp.highlight.HighlightedField;
import com.enonic.xp.highlight.HighlightedFields;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class HighlightedFieldsFactory
{
    private static final Pattern POSTFIX_PATTERN = Pattern.compile(
        "\\.(?:" + IndexValueType.DATETIME.getPostfix() + "|" + IndexValueType.NUMBER.getPostfix() + "|" +
            IndexValueType.NGRAM.getPostfix() + "|" + IndexValueType.ANALYZED.getPostfix() + "|" + IndexValueType.ORDERBY.getPostfix() +
            "|" + IndexValueType.GEO_POINT.getPostfix() + "|" + IndexValueType.PATH.getPostfix() + ")$" );

    public static HighlightedFields create( final Map<String, HighlightField> highlightFieldMap )
    {
        if ( highlightFieldMap == null )
        {
            return null;
        }

        final HighlightedFields.Builder result = HighlightedFields.create();

        for ( final Map.Entry<String, HighlightField> fieldEntry : highlightFieldMap.entrySet() )
        {
            final String fieldName = removePostfix( fieldEntry.getKey() );
            final HighlightedField.Builder builder = HighlightedField.create().
                name( fieldName );
            for ( Text fragment : fieldEntry.getValue().fragments() )
            {
                builder.addFragment( fragment.string() );
            }
            result.add( builder.build() );
        }

        return result.build();
    }

    private static String removePostfix( final String fieldName )
    {
        final Matcher matcher = POSTFIX_PATTERN.matcher( fieldName );
        if ( matcher.find() )
        {
            return fieldName.substring( 0, matcher.start() );
        }
        return fieldName;
    }
}
