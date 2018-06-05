package com.enonic.xp.cluster;

import java.net.InetAddress;
import java.util.List;

public interface NodeDiscovery
{
    List<InetAddress> get();
}
