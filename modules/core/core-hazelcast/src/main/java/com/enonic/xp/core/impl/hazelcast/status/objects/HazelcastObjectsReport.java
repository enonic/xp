package com.enonic.xp.core.impl.hazelcast.status.objects;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

class HazelcastObjectsReport
{
    private final List<MapObjectReport> mapObjectReports;

    private final List<QueueObjectReport> queueObjectReports;

    private final List<TopicObjectReport> topicObjectReports;

    private final List<ExecutorServiceObjectReport> executorServiceObjectReports;

    private final List<FencedLockObjectReport> fencedLockObjectReports;


    private HazelcastObjectsReport( Builder builder )
    {
        mapObjectReports = List.copyOf( builder.mapObjectReports );
        queueObjectReports = List.copyOf( builder.queueObjectReports );
        topicObjectReports = List.copyOf( builder.topicObjectReports );
        executorServiceObjectReports = List.copyOf( builder.executorServiceObjectReports );
        fencedLockObjectReports = List.copyOf( builder.fencedLockObjectReports );
    }

    ObjectNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        final ArrayNode mapsJson = json.putArray( "maps" );
        mapObjectReports.stream().map( MapObjectReport::toJson ).forEach( mapsJson::add );
        final ArrayNode queuesJson = json.putArray( "queues" );
        queueObjectReports.stream().map( QueueObjectReport::toJson ).forEach( queuesJson::add );
        final ArrayNode topicsJson = json.putArray( "topics" );
        topicObjectReports.stream().map( TopicObjectReport::toJson ).forEach( topicsJson::add );
        final ArrayNode executorServicesJson = json.putArray( "executorServices" );
        executorServiceObjectReports.stream().map( ExecutorServiceObjectReport::toJson ).forEach( executorServicesJson::add );
        final ArrayNode fencedLocks = json.putArray( "fencedLocks" );
        fencedLockObjectReports.stream().map( FencedLockObjectReport::toJson ).forEach( fencedLocks::add );

        return json;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        List<MapObjectReport> mapObjectReports = new ArrayList<>();

        List<QueueObjectReport> queueObjectReports = new ArrayList<>();

        List<TopicObjectReport> topicObjectReports = new ArrayList<>();

        List<ExecutorServiceObjectReport> executorServiceObjectReports = new ArrayList<>();

        List<FencedLockObjectReport> fencedLockObjectReports = new ArrayList<>();

        Builder addMapObject( MapObjectReport mapObjectReport )
        {
            mapObjectReports.add( mapObjectReport );
            return this;
        }

        Builder addQueueObject( QueueObjectReport queueObjectReport )
        {
            queueObjectReports.add( queueObjectReport );
            return this;
        }

        Builder addTopicObject( TopicObjectReport topicObjectReport )
        {
            topicObjectReports.add( topicObjectReport );
            return this;
        }

        Builder addExecutorServiceObject( ExecutorServiceObjectReport topicObjectReport )
        {
            executorServiceObjectReports.add( topicObjectReport );
            return this;
        }

        Builder addFencedLockObject( FencedLockObjectReport fencedLockObjectReport )
        {
            fencedLockObjectReports.add( fencedLockObjectReport );
            return this;
        }

        HazelcastObjectsReport build()
        {
            return new HazelcastObjectsReport( this );
        }
    }
}
