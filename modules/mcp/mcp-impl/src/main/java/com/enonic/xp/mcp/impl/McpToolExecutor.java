package com.enonic.xp.mcp.impl;

import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.descriptor.DescriptorKey;

@NullMarked
public interface McpToolExecutor
{
    Map<String, @Nullable Object> execute( DescriptorKey key, String localName, Map<String, @Nullable Object> arguments );
}
