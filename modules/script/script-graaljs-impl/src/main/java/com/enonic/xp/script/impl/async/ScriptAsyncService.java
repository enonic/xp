package com.enonic.xp.script.impl.async;

import java.util.concurrent.Executor;

import com.enonic.xp.app.ApplicationKey;

public interface ScriptAsyncService
{
    Executor getAsyncExecutor( ApplicationKey applicationKey );
}
