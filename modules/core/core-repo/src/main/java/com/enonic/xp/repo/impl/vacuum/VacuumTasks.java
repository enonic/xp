package com.enonic.xp.repo.impl.vacuum;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import com.google.common.collect.Sets;

class VacuumTasks
    implements Iterable<VacuumTask>
{
    private final TreeSet<VacuumTask> tasks;

    public VacuumTasks()
    {
        this.tasks = Sets.newTreeSet( Comparator.comparingInt( VacuumTask::order ) );
    }

    @Override
    public Iterator<VacuumTask> iterator()
    {
        return tasks.iterator();
    }

    public int size()
    {
        return this.tasks.size();
    }

    public void add( final VacuumTask task )
    {
        this.tasks.add( task );
    }

    public boolean remove( final VacuumTask task )
    {
        return this.tasks.remove( task );
    }
}
