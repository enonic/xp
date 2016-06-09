package com.enonic.xp.server.udc.impl;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class UdcInfo
{
    String uuid;

    String product;

    String version;

    String versionHash;

    String javaVersion;

    String osName;

    long maxMemory;

    int numCpu;

    String timezone;

    long upTime;

    String toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "uuid", this.uuid );
        json.put( "product", this.product );
        json.put( "version", this.version );
        json.put( "versionHash", this.versionHash );
        json.put( "javaVersion", this.javaVersion );
        json.put( "osName", this.osName );
        json.put( "maxMemory", this.maxMemory );
        json.put( "numCpu", this.numCpu );
        json.put( "timezone", this.timezone );
        json.put( "upTime", this.upTime );
        return json.toString();
    }
}
