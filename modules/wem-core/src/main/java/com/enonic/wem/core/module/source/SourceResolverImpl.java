package com.enonic.wem.core.module.source;

import com.enonic.wem.api.module.ModuleResourceKey;

/**
 * Resolve order:
 * - local module
 * - system module
 * <p/>
 * Resolve ./test.js
 * - resolve from module base
 * - else not found
 * <p/>
 * Resolve test.js
 * - resolve from module base (and osgi bundle classpath soon)
 * - else resolve from system
 * - else not found
 * <p/>
 */
public final class SourceResolverImpl
    implements SourceResolver
{
    @Override
    public ModuleSource resolve( final ModuleResourceKey key )
    {
        return null;
    }

    @Override
    public ModuleSource resolve( final ModuleResourceKey base, final String uri )
    {
        return null;
    }
}
