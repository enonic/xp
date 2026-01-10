package com.enonic.xp.vacuum;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class VacuumConstants
{
    /**
     * Attribute key to mark a node version as protected from vacuum deletion.
     * When this attribute is set on a version, the vacuum process will skip it.
     */
    public static final String PREVENT_VACUUM_ATTRIBUTE = "preventVacuum";

    private VacuumConstants()
    {
    }
}
