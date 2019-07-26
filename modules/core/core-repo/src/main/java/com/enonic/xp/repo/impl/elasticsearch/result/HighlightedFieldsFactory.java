package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Map;
import java.util.Set;

import org.elasticsearch.search.highlight.HighlightField;

import com.enonic.xp.highlight.HighlightedField;
import com.enonic.xp.highlight.HighlightedFields;

public class HighlightedFieldsFactory
{
    public static HighlightedFields create( final Map<String, HighlightField> searchHits )
    {
        if ( searchHits == null )
        {
            return null;
        }

        final HighlightedFields.Builder result = HighlightedFields.create();

        for ( final Map.Entry<String, HighlightField> fieldEntry : searchHits.entrySet() )
        {
            final HighlightedField.Builder builder = HighlightedField.create().name( fieldEntry.getKey() );

            Set.of( fieldEntry.getValue().fragments() ).
                forEach( fragment -> builder.addFragment( fragment.string() ) );

            result.add( fieldEntry.getKey(), builder.build() );
        }

        return result.build();
    }
}
