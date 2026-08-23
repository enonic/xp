package com.enonic.xp.node;

/**
 * @deprecated A node is created with the order key of its creation instant, which places it first; other placements are
 * reorders of the created node. Scheduled for removal.
 */
@Deprecated
public enum InsertManualStrategy
{
    FIRST, LAST, MANUAL
}
