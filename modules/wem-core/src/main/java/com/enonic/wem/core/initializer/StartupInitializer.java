package com.enonic.wem.core.initializer;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Lists;

public final class StartupInitializer
{
    private final List<InitializerTask> tasks;

    @Inject
    public StartupInitializer( final Set<InitializerTask> tasks )
    {
        final List<InitializerTask> sortedTaskList = Lists.newArrayList( tasks );
        Collections.sort( sortedTaskList );
        this.tasks = sortedTaskList;
    }

    public void initialize()
        throws Exception
    {
        for ( final InitializerTask task : this.tasks )
        {
            task.initialize();
        }
    }
}
