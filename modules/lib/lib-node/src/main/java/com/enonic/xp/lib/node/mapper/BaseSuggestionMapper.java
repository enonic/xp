package com.enonic.xp.lib.node.mapper;

import java.util.List;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.suggester.Suggestion;
import com.enonic.xp.suggester.SuggestionEntry;
import com.enonic.xp.suggester.SuggestionOption;

abstract class BaseSuggestionMapper<OPTION extends SuggestionOption, ENTRY extends SuggestionEntry<OPTION>, SUGGESTION extends Suggestion<ENTRY>>
    implements MapSerializable
{
    private final SUGGESTION value;

    public BaseSuggestionMapper( final SUGGESTION value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serializeSuggestion( gen, this.value );
    }

    private void serializeSuggestion( final MapGenerator gen, final SUGGESTION value )
    {
        gen.array( value.getName() );

        serializeEntries( gen, value.getEntries() );

        gen.end();

    }

    private void serializeEntries( final MapGenerator gen, final List<ENTRY> suggestionEntries )
    {
        for ( ENTRY suggestionEntry : suggestionEntries )
        {
            serializeEntry( gen, suggestionEntry );
        }
    }

    private void serializeEntry( final MapGenerator gen, final ENTRY value )
    {
        gen.map();
        this.serializeEntryFields( gen, value );
        serializeOptions( gen, value.getOptions() );
        gen.end();
    }


    private void serializeOptions( final MapGenerator gen, final List<OPTION> options )
    {
        gen.array( "options" );
        options.forEach( option -> {
            gen.map();
            this.serializeOptionFields( gen, option );
            gen.end();
        } );
        gen.end();

    }

    protected void serializeEntryFields( final MapGenerator gen, final ENTRY value )
    {
        gen.value( "text", value.getText() );
        gen.value( "length", value.getLength() );
        gen.value( "offset", value.getOffset() );
    }

    protected void serializeOptionFields( final MapGenerator gen, final OPTION value )
    {
        gen.value( "text", value.getText() );
        gen.value( "score", value.getScore() );
    }
}
