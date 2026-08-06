package com.enonic.xp.repo.impl.search.dsl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.suggester.SuggestionQuery;
import com.enonic.xp.query.suggester.TermSuggestionQuery;

/**
 * Renders one {@link SuggestionQuery} into its canonical wire form:
 * <pre>{ "text": &lt;string&gt;, "term": { "field": .., "size": .., ... } }</pre>
 * keyed under the suggester's name by {@link SearchDslRenderer}.
 * <p>
 * The shape is deliberately XP's OWN vocabulary, not the engine's — {@code suggestMode} rather
 * than {@code suggest_mode}, {@code stringDistance} rather than {@code string_distance}, and the
 * value {@code jarowinkler} rather than OpenSearch's {@code jaro_winkler}. That is the same rule
 * the constraint renderer follows for field names: the wire carries what XP means and the backend
 * owns how the engine spells it. It also happens to match the shape {@code lib-node} already
 * accepts from scripts, {@code { text, term: { ... } }} with {@code text} OUTSIDE {@code term},
 * so there is one suggester vocabulary in the system rather than two.
 * <p>
 * The field name is emitted post-{@link IndexPath} and WITHOUT a sub-field postfix. XP's ES path
 * resolves it to the analyzed variant; that resolution is a physical-layout decision and belongs
 * to the backend (on the nodb path the analyzed variant is not even spelled the same way).
 * <p>
 * {@code TermSuggestionQuery} is the only suggester type XP defines — there is no completion or
 * phrase suggester anywhere in the codebase, and the ES factory throws on anything else — so an
 * unknown subtype fails loudly here rather than being rendered as a term suggester that silently
 * ignores its own parameters.
 */
final class SuggestDslRenderer
{
    private SuggestDslRenderer()
    {
    }

    static Map<String, Object> render( final SuggestionQuery query )
    {
        if ( !( query instanceof TermSuggestionQuery ) )
        {
            throw new DslRenderException( "Unexpected suggestion type: " + query.getClass() );
        }
        final TermSuggestionQuery term = (TermSuggestionQuery) query;

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put( "field", IndexPath.from( term.getField() ).getPath() );
        put( body, "size", term.getSize() );
        put( body, "analyzer", term.getAnalyzer() );
        put( body, "sort", term.getSort() == null ? null : term.getSort().value() );
        put( body, "suggestMode", term.getSuggestMode() == null ? null : term.getSuggestMode().value() );
        put( body, "maxEdits", term.getMaxEdits() );
        put( body, "prefixLength", term.getPrefixLength() );
        put( body, "minWordLength", term.getMinWordLength() );
        put( body, "maxInspections", term.getMaxInspections() );
        put( body, "minDocFreq", term.getMinDocFreq() );
        put( body, "maxTermFreq", term.getMaxTermFreq() );
        put( body, "stringDistance", term.getStringDistance() == null ? null : term.getStringDistance().value() );

        final Map<String, Object> suggester = new LinkedHashMap<>();
        suggester.put( "text", term.getText() );
        suggester.put( "term", body );
        return suggester;
    }

    /**
     * Absent means absent. Every one of these parameters is null-guarded on the ES path too, and an
     * unset suggester parameter is the engine's own default — writing the default out instead would
     * make this renderer own a value it has no opinion about.
     */
    private static void put( final Map<String, Object> body, final String name, final Object value )
    {
        if ( value != null )
        {
            body.put( name, value );
        }
    }
}
