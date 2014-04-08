package com.enonic.wem.core.script.engine;

import javax.script.ScriptException;

import com.google.common.base.Throwables;

import com.enonic.wem.core.module.source.ModuleSource;
import com.enonic.wem.core.module.source.SourceProblemException;

final class ScriptEngineHelper
{
    public static SourceProblemException handleError( final ModuleSource source, final ScriptException e )
    {
        final SourceProblemException.Builder builder = SourceProblemException.newBuilder();

        final Throwable cause = Throwables.getRootCause( e );
        builder.cause( cause );
        builder.lineNumber( e.getLineNumber() );
        builder.source( source );
        builder.message( cause.getMessage() );
        return builder.build();
    }
}
