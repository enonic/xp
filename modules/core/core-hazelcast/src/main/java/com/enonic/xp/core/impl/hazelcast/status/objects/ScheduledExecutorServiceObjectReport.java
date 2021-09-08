package com.enonic.xp.core.impl.hazelcast.status.objects;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

class ScheduledExecutorServiceObjectReport
{
    private final String name;

    private final List<ScheduledTaskReport> tasks;

    private ScheduledExecutorServiceObjectReport( Builder builder )
    {
        name = builder.name;
        tasks = List.copyOf( builder.tasks );
    }

    ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "name", name );
        final ArrayNode tasksArray = json.putArray( "tasks" );

        tasks.forEach( task -> {
            final ObjectNode taskJson = JsonNodeFactory.instance.objectNode();
            taskJson.put( "member", task.getMember() );
            taskJson.put( "taskName", task.getTaskName() );
            taskJson.put( "totalRuns", task.getTotalRuns() );

            tasksArray.add( taskJson );
        } );
        return json;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        String name;

        List<ScheduledTaskReport> tasks = new ArrayList<>();

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder task( final ScheduledTaskReport task )
        {
            this.tasks.add( task );
            return this;
        }

        ScheduledExecutorServiceObjectReport build()
        {
            return new ScheduledExecutorServiceObjectReport( this );
        }
    }
}
