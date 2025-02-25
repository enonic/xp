package com.enonic.xp.server.impl.status;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.server.BuildInfo;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class ServerReporter
    extends JsonStatusReporter
{
    ServerInfo serverInfo;

    public ServerReporter()
    {
        this.serverInfo = ServerInfo.get();
    }

    @Override
    public String getName()
    {
        return "server";
    }

    @Override
    public JsonNode getReport()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "version", VersionInfo.get().getVersion() );
        json.put( "installation", this.serverInfo.getName() );
        json.put( "runMode", RunMode.get().toString() );
        json.set( "build", buildBuildInfo() );
        return json;
    }

    private ObjectNode buildBuildInfo()
    {
        final BuildInfo info = this.serverInfo.getBuildInfo();

        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put( "hash", info.getHash() );
        node.put( "shortHash", info.getShortHash() );
        node.put( "branch", info.getBranch() );
        node.put( "timestamp", info.getTimestamp() );

        return node;
    }
}
