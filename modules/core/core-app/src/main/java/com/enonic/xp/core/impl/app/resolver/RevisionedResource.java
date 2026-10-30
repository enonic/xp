package com.enonic.xp.core.impl.app.resolver;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.resource.Resource;

/**
 * A resource that carries the revision of the content it serves, as the resolver serving it knows it: the modification time of a
 * file, the version of a node. Equal revisions mean the same content, a change of the content means a new revision. A revision is
 * compared, never interpreted: nothing about it but its equality means anything to a reader.
 */
public interface RevisionedResource
    extends Resource
{
    /**
     * @return the revision of the content served, {@code null} when the resource does not exist
     */
    @Nullable
    Object revision();
}
