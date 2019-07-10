package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.suggester.TermSuggestion;
import com.enonic.xp.suggester.TermSuggestionEntry;
import com.enonic.xp.suggester.TermSuggestionOption;

final class TermSuggestionMapper
    extends BaseSuggestionMapper<TermSuggestionOption, TermSuggestionEntry, TermSuggestion>
{
    public TermSuggestionMapper( final TermSuggestion value )
    {
        super( value );
    }

    @Override
    protected void serializeEntryFields( final MapGenerator gen, final TermSuggestionEntry value )
    {
        super.serializeEntryFields( gen, value );
    }

    @Override
    protected void serializeOptionFields( final MapGenerator gen, final TermSuggestionOption value )
    {
        super.serializeOptionFields( gen, value );
        gen.value( "freq", value.getFreq() );
    }
}
