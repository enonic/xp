package com.enonic.xp.storage.spi;

import java.util.Map;

import org.jspecify.annotations.NullMarked;

/**
 * Phase-0-provisional per-repo index settings, opaque to the SPI (engine-shaped settings
 * stay backend-internal; the ES backend maps this onto today's {@code IndexSettings}).
 */
@NullMarked
public record IndexSettingsRecord(Map<String, Object> settings)
{
    public IndexSettingsRecord
    {
        settings = Map.copyOf( settings );
    }
}
