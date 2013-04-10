package com.enonic.wem.core.initializer;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.core.jcr.loader.JcrInitializer;
import com.enonic.wem.core.lifecycle.InitializingBean;

@Component
public final class StartupInitializer
    implements InitializingBean
{
    private JcrInitializer jcrInitializer;

    private List<InitializerTask> tasks;

    @Inject
    public void setJcrInitializer( final JcrInitializer jcrInitializer )
    {
        this.jcrInitializer = jcrInitializer;
    }

    @Inject
    public void setTasks( final Set<InitializerTask> tasks )
    {
        final List<InitializerTask> sortedTaskList = Lists.newArrayList( tasks );
        Collections.sort( sortedTaskList, new AnnotationAwareOrderComparator() );
        this.tasks = sortedTaskList;
    }

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        initialize( false );
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
