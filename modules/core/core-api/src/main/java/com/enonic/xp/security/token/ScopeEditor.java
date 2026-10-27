package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

/**
 * Changes a scope in place: sets on the editable copy what should change, and leaves the rest.
 */
@NullMarked
@FunctionalInterface
public interface ScopeEditor
{
    void edit( EditableScope edit );
}
