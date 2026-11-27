package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

/**
 * The scopes defined here: {@link XpScopes XP's own} and those the installed applications define
 * in their application descriptors.
 */
@PublicApi
@NullMarked
public interface ScopeService
{
    /**
     * Every defined scope, XP's first.
     */
    Scopes getScopes();
}
