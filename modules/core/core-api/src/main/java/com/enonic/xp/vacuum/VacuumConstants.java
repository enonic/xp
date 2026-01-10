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
     * When this attribute is set to {@code true} on a version, the vacuum process will skip it
     * and preserve the version even if it would normally be eligible for deletion.
     * <p>
     * Example usage:
     * <pre>
     * {@code
     * nodeService.applyVersionAttributes(
     *     ApplyVersionAttributesParams.create()
     *         .nodeVersionId(versionId)
     *         .addAttributes(Attributes.create()
     *             .attribute(VacuumConstants.PREVENT_VACUUM_ATTRIBUTE, GenericValue.booleanValue(true))
     *             .build())
     *         .build());
     * }
     * </pre>
     *
     * @see com.enonic.xp.node.NodeService#applyVersionAttributes(com.enonic.xp.node.ApplyVersionAttributesParams)
     */
    public static final String PREVENT_VACUUM_ATTRIBUTE = "preventVacuum";

    private VacuumConstants()
    {
    }
}
