package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.ReindexResult;

public final class ReindexResultJson
{
    public String duration;

    public Instant startTime;

    public Instant endTime;

    public int numberReindexed;

    public List<String> branches;

    public String repositoryId;

    public static ReindexResultJson create( final ReindexResult result )
    {
        final ReindexResultJson json = new ReindexResultJson();
        json.duration = result.getDuration().toString();
        json.startTime = result.getStartTime();
        json.endTime = result.getEndTime();
        json.repositoryId = result.getRepositoryId().toString();
        json.numberReindexed = result.getReindexNodes().getSize();

        json.branches = new ArrayList<>();
        for ( final Branch branch : result.getBranches() )
        {
            json.branches.add( branch.getValue() );
        }

        return json;
    }

    @Override
    public String toString()
    {
        ObjectNode node = new ObjectMapper().valueToTree( this );
        // instants are not formatted nicely by default so doing it manually
        JsonNodeFactory factory = JsonNodeFactory.instance;
        node.set( "startTime", factory.textNode( startTime.toString() ) );
        node.set( "endTime", factory.textNode( endTime.toString() ) );
        return node.toString();
    }
}
