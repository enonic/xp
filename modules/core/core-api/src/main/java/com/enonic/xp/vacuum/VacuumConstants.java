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
     * and preserve the version even if it would normally be eligible for deletion.
     * The attribute value is not checked, only its presence matters.
     * <p>
     * This attribute is automatically added to versions created during content updates.
     *
     * @see com.enonic.xp.node.NodeService#applyVersionAttributes(com.enonic.xp.node.ApplyVersionAttributesParams)
     */
    public static final String PREVENT_VACUUM_ATTRIBUTE = "vacuum.skip";

    private VacuumConstants()
    {
    }
}
