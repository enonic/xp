package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.suggester.Suggestion;
import com.enonic.xp.suggester.Suggestions;
import com.enonic.xp.suggester.TermSuggestion;

final class SuggestionsMapper
    implements MapSerializable
{
    private final Suggestions value;

    public SuggestionsMapper( final Suggestions value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serializeSuggestions( gen, this.value );
    }

    private static void serializeSuggestions( final MapGenerator gen, final Suggestions value )
    {
        for ( Suggestion suggestion : value )
        {
            if ( suggestion instanceof TermSuggestion )
            {
                new TermSuggestionMapper( (TermSuggestion) suggestion ).serialize( gen );
            }
        }
    }
}
