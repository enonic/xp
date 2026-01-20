package com.enonic.xp.vacuum;

import com.enonic.xp.annotation.PublicApi;

/**
 * Constants related to the vacuum process.
 */
@PublicApi
public final class VacuumConstants
{
    /**
     * Attribute key to mark a node version as protected from vacuum deletion.
     * <p>
     * When this attribute is present on a version, the vacuum process will skip it
     * and preserve the version.
     * Nevertheless, the version is deleted if its node is no longer part of any repository branch.
     *
     * @see com.enonic.xp.node.NodeService#applyVersionAttributes(com.enonic.xp.node.ApplyVersionAttributesParams)
     */
    public static final String VACUUM_SKIP_ATTRIBUTE = "vacuum.skip";

    private VacuumConstants()
    {
    }
}
