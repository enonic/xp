package com.enonic.xp.cluster;

import java.util.List;

public interface NodeDiscovery<T>
{
    List<T> get();
}
