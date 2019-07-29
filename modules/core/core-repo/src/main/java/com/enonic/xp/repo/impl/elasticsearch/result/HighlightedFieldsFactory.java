package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Map;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.highlight.HighlightField;

import com.enonic.xp.highlight.HighlightedField;
import com.enonic.xp.highlight.HighlightedFields;

public class HighlightedFieldsFactory
{
    public static HighlightedFields create( final Map<String, HighlightField> highlightFieldMap )
    {
        if ( highlightFieldMap == null )
        {
            return null;
        }

        final HighlightedFields.Builder result = HighlightedFields.create();

        for ( final Map.Entry<String, HighlightField> fieldEntry : highlightFieldMap.entrySet() )
        {
            final HighlightedField.Builder builder = HighlightedField.create().
                name( fieldEntry.getKey() );
            for ( Text fragment : fieldEntry.getValue().fragments() )
            {
                builder.addFragment( fragment.string() );
            }
            result.add( builder.build() );
        }

        return result.build();
    }
}
