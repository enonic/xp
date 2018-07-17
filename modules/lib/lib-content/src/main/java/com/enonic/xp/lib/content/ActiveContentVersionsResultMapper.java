package com.enonic.xp.lib.content;

import com.google.common.collect.ImmutableSortedSet;

import com.enonic.xp.content.ActiveContentVersionEntry;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.lib.content.mapper.ContentVersionMapper;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ActiveContentVersionsResultMapper
    implements MapSerializable
{
    private final ImmutableSortedSet<ActiveContentVersionEntry> activeContentVersions;

    public ActiveContentVersionsResultMapper( final GetActiveContentVersionsResult result )
    {
        this.activeContentVersions = result.getActiveContentVersions();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        for ( ActiveContentVersionEntry activeContentVersionEntry : activeContentVersions )
        {
            gen.value( activeContentVersionEntry.getBranch().toString(),
                       new ContentVersionMapper( activeContentVersionEntry.getContentVersion() ) );
        }
    }
}
