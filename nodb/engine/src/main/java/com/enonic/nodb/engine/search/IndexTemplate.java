package com.enonic.nodb.engine.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Loads {@code nodb/opensearch/index-template.json} — the ported mapping/analyzer settings —
 * and turns it into a create-index body.
 *
 * <p>The resource is the port of XP's {@code search-settings.json} (600 lines) and
 * {@code search-mapping.json} (91 dynamic templates) onto OpenSearch 3.7, nodb-path only:
 * XP's own resources are untouched, so embedded ES keeps its layout byte-identical and no
 * existing install needs a reindex. See the resource's own {@code _doc} block for the
 * divergence record (D1 {@code _text}, D1b {@code _fulltext}, D8 collation keys, blocker 2's
 * {@code path_match}, the deleted {@code _all}/{@code _default_}/{@code standard}-filter, the
 * raised limits).
 *
 * <p>NoDB does not rely on a server-side composable template: the alias is per-index
 * ({@code <tenant>-<repo>}) and a composable template can only carry a static alias, so the
 * alias must be attached in the create call. Hence "template" here means "the body NoDB PUTs",
 * not "an OpenSearch index template".
 */
public final class IndexTemplate
{
    private static final String RESOURCE = "nodb/opensearch/index-template.json";

    /**
     * Human-readable port record inside the resource. Stripped before the body is sent —
     * OpenSearch rejects unknown top-level keys in a create-index body, and the rationale
     * belongs next to the mapping it explains, not in a separate file that drifts from it.
     */
    private static final String DOC_KEY = "_doc";

    private final ObjectNode root;

    private IndexTemplate( ObjectNode root )
    {
        this.root = root;
    }

    public static IndexTemplate load()
    {
        try (InputStream in = IndexTemplate.class.getClassLoader().getResourceAsStream( RESOURCE ))
        {
            if ( in == null )
            {
                throw new IllegalStateException( "Missing index template on classpath: " + RESOURCE );
            }
            return new IndexTemplate( (ObjectNode) OpenSearchClient.mapper().readTree( in ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    /**
     * The version stamped into {@code mappings._meta.templateVersion} of every index created
     * from this template, and recorded per generation in {@code search_index}. It travels into
     * the live index on purpose: a generation can then be asked which template built it, which
     * is what makes "a template change is a new generation, never an in-place mutation" an
     * auditable rule rather than a convention.
     */
    public int templateVersion()
    {
        return root.path( "mappings" ).path( "_meta" ).path( "templateVersion" ).asInt( 0 );
    }

    /** Read-only view for consistency tests. */
    public JsonNode raw()
    {
        return root;
    }

    /**
     * A complete create-index body: the ported settings + mappings, with the two
     * per-environment settings substituted and the alias attached.
     */
    public ObjectNode createIndexBody( String alias, OpenSearchConfig config )
    {
        ObjectNode body = root.deepCopy();
        body.remove( DOC_KEY );

        ObjectNode index = (ObjectNode) body.path( "settings" ).path( "index" );
        index.put( "number_of_replicas", config.replicas() );
        index.put( "refresh_interval", config.refreshInterval() );

        body.putObject( "aliases" ).putObject( alias );
        return body;
    }
}
