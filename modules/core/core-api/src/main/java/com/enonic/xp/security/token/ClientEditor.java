package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

/**
 * Changes a client in place: sets on the editable copy what should change, and leaves the rest.
 */
@NullMarked
@FunctionalInterface
public interface ClientEditor
{
    void edit( EditableClient edit );
}
