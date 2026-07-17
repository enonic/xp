package com.enonic.nodb.engine;

/**
 * Marker/placeholder for the NoDB engine module. Real content (stores, WriteBatch,
 * tenant provisioning) lands in later slice-1 steps; this class exists so the module
 * compiles and CI has something to gate on before step 2 lands.
 */
public final class NodbEngine
{
    public static final String VERSION = "0.1.0-SNAPSHOT";

    private NodbEngine()
    {
    }
}
