package com.enonic.wem.core.module;

import java.nio.file.Path;

import com.google.inject.ImplementedBy;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;

@ImplementedBy(ModuleResourcePathResolverImpl.class)
public interface ModuleResourcePathResolver
{
    Path resolveModulePath( ModuleKey moduleKey );

    Path resolveResourcePath( ModuleResourceKey moduleResource );
}
