package com.enonic.xp.storage.spi;

import java.util.Map;

import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

/**
 * Phase-0-provisional search-document shape: an indexable node, keyed by node id, with its
 * indexed fields. This is a minimal stand-in — the existing ES-free document family
 * ({@code IndexDocument} and {@code IndexItems} in core-repo) is the real shape and moves
 * into this module in Gate C, superseding this record.
 */
@NullMarked
public record IndexDocumentRecord(String id, Map<String, Object> fields)
{
    public IndexDocumentRecord
    {
        requireNonNull( id );
        fields = Map.copyOf( fields );
    }
}
