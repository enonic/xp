package com.enonic.wem.core.initializer;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import com.enonic.wem.core.jcr.loader.JcrInitializer;
import com.enonic.wem.core.lifecycle.LifecycleBean;
import com.enonic.wem.core.lifecycle.RunLevel;

public final class StartupInitializer
    extends LifecycleBean
{
    private JcrInitializer jcrInitializer;

    private List<InitializerTask> tasks;

    public StartupInitializer()
    {
        super( RunLevel.L5 );
    }

    @Inject
    public void setJcrInitializer( final JcrInitializer jcrInitializer )
    {
        this.jcrInitializer = jcrInitializer;
    }

    @Inject
    public void setTasks( final Set<InitializerTask> tasks )
    {
        final List<InitializerTask> sortedTaskList = Lists.newArrayList( tasks );
        Collections.sort( sortedTaskList );
        this.tasks = sortedTaskList;
    }

    @Override
    protected void doStart()
        throws Exception
    {
        initialize( false );
    }

    @Override
    protected void doStop()
        throws Exception
    {
        // Do nothing
    }

    private void initializeTasks()
        throws Exception
    {
        for ( final InitializerTask task : this.tasks )
        {
            task.initialize();
        }
    }

    public void initialize( final boolean reInit )
        throws Exception
    {
        if ( this.jcrInitializer.initialize( reInit ) )
        {
            initializeTasks();
        }
    }
}
