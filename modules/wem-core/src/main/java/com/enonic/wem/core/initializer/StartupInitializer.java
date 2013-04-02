package com.enonic.wem.core.initializer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.core.annotation.Order;
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
        Collections.sort( sortedTaskList, new Comparator<InitializerTask>()
        {
            @Override
            public int compare( final InitializerTask it1, final InitializerTask it2 )
            {
                final Order order1 = it1.getClass().getAnnotation( Order.class );
                final Order order2 = it2.getClass().getAnnotation( Order.class );
                return order1.value() - order2.value();
            }
        } );
        this.tasks = sortedTaskList;
    }

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        if ( this.jcrInitializer.initialize() )
        {
            initializeTasks();
        }
    }

    private void initializeTasks()
        throws Exception
    {
        for ( final InitializerTask task : this.tasks )
        {
            task.initialize();
        }
    }
}
