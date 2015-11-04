package com.enonic.xp.core.impl.status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.server.BuildInfo;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true)
public final class ServerReporter
    implements StatusReporter
{
    private ServerInfo serverInfo;

    @Override
    public String getName()
    {
        return "server";
    }

    @Override
    public ObjectNode getReport()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "version", VersionInfo.get().getVersion() );
        json.put( "installation", this.serverInfo.getName() );
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

    @Reference
    public void setServerInfo( final ServerInfo serverInfo )
    {
        this.serverInfo = serverInfo;
    }
}
