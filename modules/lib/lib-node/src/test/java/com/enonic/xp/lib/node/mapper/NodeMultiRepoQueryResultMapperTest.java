package com.enonic.xp.lib.node.mapper;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.query.QueryExplanation;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.suggester.Suggestions;
import com.enonic.xp.suggester.TermSuggestion;
import com.enonic.xp.suggester.TermSuggestionEntry;
import com.enonic.xp.suggester.TermSuggestionOption;
import com.enonic.xp.testing.serializer.JsonMapGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeMultiRepoQueryResultMapperTest
{

    @Test
    void testSerialize()
    {

        final FindNodesByMultiRepoQueryResult result = FindNodesByMultiRepoQueryResult.create().suggestions( Suggestions.create().add(
            TermSuggestion.create( "termSuggestion" ).addSuggestionEntry(
                TermSuggestionEntry.create().text( "text" ).length( 1 ).offset( 1 ).addSuggestionOption(
                    TermSuggestionOption.create().text( "text-1" ).score( 1.0f ).freq( 2 ).build() ).addSuggestionOption(
                    TermSuggestionOption.create().text( "text-2" ).score( 4.0f ).freq(
                        5 ).build() ).build() ).build() ).build() ).addNodeHit(
            MultiRepoNodeHit.create().nodeId( NodeId.from( "abc" ) ).branch( Branch.from( "fisk" ) ).repositoryId(
                RepositoryId.from( "repo" ) ).explanation( QueryExplanation.create().description( "myDescription" ).value( 123L ).addDetail(
                QueryExplanation.create().description( "myDescription" ).value( 123L ).build() ).build() ).build() ).build();

        final NodeMultiRepoQueryResultMapper mapper = new NodeMultiRepoQueryResultMapper( result );

        final JsonMapGenerator gen = new JsonMapGenerator();

        mapper.serialize( gen );

        JsonNode actualJson = (JsonNode) gen.getRoot();

        JsonNode suggestions = actualJson.get( "suggestions" );

        JsonNode termSuggestion = suggestions.get( "termSuggestion" );
        assertNotNull( termSuggestion );
        assertTrue( termSuggestion.isArray() );
        assertEquals( 1, termSuggestion.size() );

        JsonNode suggestionEntry = termSuggestion.get( 0 );

        assertEquals( "text", suggestionEntry.get( "text" ).asText() );
        assertEquals( 1, suggestionEntry.get( "length" ).asInt() );
        assertEquals( 1, suggestionEntry.get( "offset" ).asInt() );
        assertNotNull( suggestionEntry.get( "options" ) );
        assertEquals( 2, suggestionEntry.get( "options" ).size() );

        JsonNode suggestionEntryOption1 = suggestionEntry.get( "options" ).get( 0 );

        assertEquals( "text-1", suggestionEntryOption1.get( "text" ).asText() );
        assertEquals( 1.0, suggestionEntryOption1.get( "score" ).asDouble() );
        assertEquals( 2, suggestionEntryOption1.get( "freq" ).asInt() );

        JsonNode suggestionEntryOption2 = suggestionEntry.get( "options" ).get( 1 );

        assertEquals( "text-2", suggestionEntryOption2.get( "text" ).asText() );
        assertEquals( 4.0, suggestionEntryOption2.get( "score" ).asDouble() );
        assertEquals( 5, suggestionEntryOption2.get( "freq" ).asInt() );
    }
}
