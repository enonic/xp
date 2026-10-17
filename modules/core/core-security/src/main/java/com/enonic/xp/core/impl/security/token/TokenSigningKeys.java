package com.enonic.xp.core.impl.security.token;

import java.util.Optional;

/**
 * Where the token service gets its keys: one to sign with, and any number to verify with. Keeping
 * verification keys beyond the signing key is what lets a rotation keep already-issued tokens valid.
 */
interface TokenSigningKeys
{
    /**
     * The key new tokens are signed with.
     */
    SigningKey signingKey();

    /**
     * The key with the given id, for verification; empty when unknown, in which case the token
     * is rejected.
     */
    Optional<SigningKey> verificationKey( String kid );
}
