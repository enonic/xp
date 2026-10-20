package com.enonic.xp.core.impl.security.token;

import java.util.Optional;

/**
 * Where the token service gets its keys: one to sign with, any number to verify with. Keeping old
 * verification keys is what lets a rotation keep issued tokens valid.
 */
interface TokenSigningKeys
{
    /**
     * The key new tokens are signed with.
     */
    SigningKey signingKey();

    /**
     * The key with the given id, or empty if unknown, in which case the token is rejected.
     */
    Optional<SigningKey> verificationKey( String kid );
}
