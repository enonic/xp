package com.enonic.wem.core.module;

import java.nio.file.Path;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;

public interface ModuleResourcePathResolver
{
    Path resolveModulePath( ModuleKey moduleKey );

    Path resolveResourcePath( ModuleResourceKey moduleResource );
}
