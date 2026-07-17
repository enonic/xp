package com.enonic.xp.storage.spi;

import java.util.Collection;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Indexable search-document shape: a node, keyed by node id, with its indexed fields (each
 * field name potentially multi-valued, matching one node property fanned out into several
 * indexed variants — e.g. analyzed/ngram/order-by/stemmed forms of the same value) and an
 * optional per-document analyzer.
 * <p>
 * Gate-C deviation from the Gate-A plan: core-repo's {@code IndexDocument}/{@code IndexItems}
 * family is NOT moved into the SPI as-is. Building it requires the ES-index-multi-field
 * naming convention (value-type postfixes such as {@code _ngram}/{@code _analyzed}/
 * {@code _orderby}) and a ~30-language stemming/analyzer table
 * ({@code com.enonic.xp.repo.impl.index.IndexLanguageController}) that are consumed by two
 * dozen other Elasticsearch query-translation classes — genuinely backend-shaped, not a
 * generic SPI concept. Instead, this record is the permanent boundary shape: core-repo
 * builds its {@code IndexDocument} exactly as before and flattens it to this record
 * ({@code IndexItems.asValuesMap()} plus the analyzer) only at the {@link NodeSearchIndex}
 * call.
 */
@NullMarked
public record IndexDocumentRecord(String id, @Nullable String analyzer, Map<String, Collection<Object>> fields)
{
    public IndexDocumentRecord
    {
        requireNonNull( id );
        fields = Map.copyOf( fields );
    }
}
