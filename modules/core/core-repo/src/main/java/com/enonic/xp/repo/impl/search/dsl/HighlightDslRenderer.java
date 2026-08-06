package com.enonic.xp.repo.impl.search.dsl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.highlight.HighlightPropertySettings;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryProperty;
import com.enonic.xp.query.highlight.HighlightQuerySettings;
import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;

/**
 * Renders a {@link HighlightQuery} into its canonical wire form:
 * <pre>{ "settings": { .. }, "properties": [ { "name": .., "settings": { .. } } ] }</pre>
 * <p>
 * Two things are deliberately NOT done here, both because they are physical-layout decisions and
 * this renderer sits above the storage SPI:
 * <ol>
 * <li><b>The three-field expansion.</b> XP's ES path expands each property into
 * {@code name}, {@code name._analyzed} and {@code name._ngram} — dotted, contrary to the Gate 0(c)
 * inventory's "underscore, no dot" note, which a reading of
 * {@code IndexValueType.INDEX_VALUE_TYPE_SEPARATOR} settles. Those postfixes are not the same on
 * the nodb path ({@code ._text}/{@code ._fulltext}/{@code ._ngram}), so the wire carries the bare
 * property name and the backend expands it.</li>
 * <li><b>{@code type: plain}.</b> The ES path forces it unconditionally at request level; it is a
 * highlighter-implementation choice, so the backend applies it.</li>
 * </ol>
 * The global block and the per-property block are rendered by the same code because XP's own
 * {@code HighlightQuerySettings} IS a {@code HighlightPropertySettings} plus {@code encoder} and
 * {@code tagsSchema} — the two settings that exist only globally.
 */
final class HighlightDslRenderer
{
    private HighlightDslRenderer()
    {
    }

    /**
     * @return the canonical config, or {@code null} when the query highlights nothing. An empty
     * {@code HighlightQuery} is what {@code lib-node} passes for a query with no highlight block at
     * all, so it must mean "no highlighting" rather than "highlight with defaults" — and the ES
     * path agrees, since a highlight with no fields highlights nothing.
     */
    static Map<String, Object> render( final HighlightQuery highlight )
    {
        if ( highlight == null || highlight.getProperties().isEmpty() )
        {
            return null;
        }

        final List<Object> properties = new ArrayList<>( highlight.getProperties().size() );
        for ( final HighlightQueryProperty property : highlight.getProperties() )
        {
            final Map<String, Object> rendered = new LinkedHashMap<>();
            rendered.put( "name", IndexPath.from( property.getName() ).getPath() );
            // HighlightQueryProperty exposes its settings only through delegating getters, so the
            // per-property block is read off the property itself rather than off a settings object.
            final Map<String, Object> settings =
                renderSettings( property.getFragmenter(), property.getFragmentSize(), property.getNoMatchSize(),
                                property.getNumOfFragments(), property.getOrder(), property.getPreTags(), property.getPostTags(),
                                property.getRequireFieldMatch() );
            if ( !settings.isEmpty() )
            {
                rendered.put( "settings", settings );
            }
            properties.add( rendered );
        }

        final Map<String, Object> result = new LinkedHashMap<>();
        final Map<String, Object> global = renderGlobalSettings( highlight.getSettings() );
        if ( !global.isEmpty() )
        {
            result.put( "settings", global );
        }
        result.put( "properties", properties );
        return result;
    }

    private static Map<String, Object> renderGlobalSettings( final HighlightQuerySettings settings )
    {
        final Map<String, Object> rendered = renderSettings( settings );
        if ( settings == null )
        {
            return rendered;
        }
        if ( settings.getEncoder() != null )
        {
            rendered.put( "encoder", settings.getEncoder().value() );
        }
        if ( settings.getTagsSchema() != null )
        {
            rendered.put( "tagsSchema", settings.getTagsSchema().value() );
        }
        return rendered;
    }

    private static Map<String, Object> renderSettings( final HighlightPropertySettings settings )
    {
        if ( settings == null )
        {
            return new LinkedHashMap<>();
        }
        return renderSettings( settings.getFragmenter(), settings.getFragmentSize(), settings.getNoMatchSize(),
                               settings.getNumOfFragments(), settings.getOrder(), settings.getPreTags(), settings.getPostTags(),
                               settings.getRequireFieldMatch() );
    }

    private static Map<String, Object> renderSettings( final Fragmenter fragmenter, final Integer fragmentSize, final Integer noMatchSize,
                                                       final Integer numOfFragments, final Order order, final List<String> preTags,
                                                       final List<String> postTags, final Boolean requireFieldMatch )
    {
        final Map<String, Object> rendered = new LinkedHashMap<>();
        if ( fragmenter != null )
        {
            rendered.put( "fragmenter", fragmenter.value() );
        }
        if ( fragmentSize != null )
        {
            rendered.put( "fragmentSize", fragmentSize );
        }
        if ( noMatchSize != null )
        {
            rendered.put( "noMatchSize", noMatchSize );
        }
        if ( numOfFragments != null )
        {
            rendered.put( "numOfFragments", numOfFragments );
        }
        if ( order != null )
        {
            rendered.put( "order", order.value() );
        }
        if ( preTags != null && !preTags.isEmpty() )
        {
            rendered.put( "preTags", List.copyOf( preTags ) );
        }
        if ( postTags != null && !postTags.isEmpty() )
        {
            rendered.put( "postTags", List.copyOf( postTags ) );
        }
        if ( requireFieldMatch != null )
        {
            rendered.put( "requireFieldMatch", requireFieldMatch );
        }
        return rendered;
    }
}
