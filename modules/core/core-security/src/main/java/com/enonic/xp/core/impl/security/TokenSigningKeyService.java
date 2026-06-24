package com.enonic.xp.core.impl.security;

import javax.crypto.SecretKey;

import org.jspecify.annotations.NullMarked;

/**
 * Resolves and manages the symmetric signing keys used for self-issued access tokens
 * (e.g. device-login tokens). This is an internal collaborator of core-security - it is
 * deliberately <b>not</b> part of the public security API, because it deals in raw key
 * material ({@link SecretKey}) that must not leak onto {@code SecurityService}.
 * <p>
 * Key material is generated once and stored in the system repository (so it is shared across
 * the cluster). The <i>effective</i> key returned by {@link #getSigningKey(String)} is derived
 * from the stored material and the optional {@code encryption_key} configured for the security
 * bundle:
 * <ul>
 *     <li>When no encryption key is configured, the stored material is used directly
 *     (backwards compatible).</li>
 *     <li>When an encryption key is configured, the effective key is derived from it, so two
 *     environments that share identical stored material (e.g. a production repository copied to
 *     QA) still end up with different effective keys. The stored material on its own is therefore
 *     not sufficient to forge or verify a token.</li>
 * </ul>
 * Keys are stored flat under {@code /keys}; each node's name is its {@code kid} and a
 * {@code preferred} field marks the single key currently used for <i>signing</i>. All present keys
 * remain valid for <i>verification</i>, which supports rotation with an overlap window.
 */
@NullMarked
public interface TokenSigningKeyService
{
    /**
     * @return the {@code kid} of the key currently used for signing (the preferred key).
     */
    String getCurrentKeyId();

    /**
     * Resolves the effective signing/verification key for the given key id.
     *
     * @param kid the key id, as carried in a token's JOSE {@code kid} header. It must reference a
     *            known signing key; arbitrary node paths are rejected.
     * @return the derived secret key (an {@code HmacSHA512} key).
     * @throws IllegalArgumentException if the key id is not a known signing key.
     */
    SecretKey getSigningKey( String kid );

    /**
     * Generates a new signing key, marks it preferred and demotes the previous preferred key to
     * verify-only. New tokens are signed with the new key; tokens signed by the previous key keep
     * verifying until that key is decommissioned.
     *
     * @return the {@code kid} of the new key.
     */
    String rotate();

    /**
     * Permanently deletes a key. The key must not be the preferred (signing) key - rotate first.
     * After deletion, tokens carrying this {@code kid} no longer verify.
     *
     * @throws IllegalStateException if the key is the current preferred key.
     */
    void decommission( String keyId );
}
