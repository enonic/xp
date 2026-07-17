/**
 * Internal, provisional storage SPI for the XP node/repo layer.
 * <p>
 * This is Phase 0 of the storage-SPI extraction: the existing embedded-Elasticsearch
 * code in {@code core-repo} is being refactored to implement this SPI with zero
 * behavior change, ahead of a second, pluggable backend. Not an app-facing API — no
 * compatibility guarantees across XP versions while the extraction is in progress.
 */
package com.enonic.xp.storage.spi;
