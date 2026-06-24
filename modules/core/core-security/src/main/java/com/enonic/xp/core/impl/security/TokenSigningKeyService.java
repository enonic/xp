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
 * Keys are stored flat under {@code /keys}; each node's name is its {@code kid}. There is no
 * "preferred" flag (which would assume a single coordinated writer): any <i>live</i>
 * (non-{@code decommissioned}) key is a valid signing key, and all live keys verify. Rotation is
 * therefore purely additive and needs no cluster coordination - a key is retired by flagging it
 * {@code decommissioned}, after which it neither signs nor verifies.
 */
@NullMarked
public interface TokenSigningKeyService
{
    /**
     * @return the {@code kid} to sign with - any live (non-decommissioned) key (the newest, for a
     * stable choice).
     */
    String getCurrentKeyId();

    /**
     * Resolves the effective verification/signing key for the given key id.
     *
     * @param kid the key id, as carried in a token's JOSE {@code kid} header. It must reference a
     *            known, live signing key; arbitrary node paths are rejected.
     * @return the derived secret key (an {@code HmacSHA512} key).
     * @throws IllegalArgumentException if the key id is not a known, live signing key.
     */
    SecretKey getSigningKey( String kid );

    /**
     * Generates and stores a new live signing key. Purely additive: no existing key is changed, so
     * concurrent rotations on different cluster nodes are safe (you simply end up with more live
     * keys, which is fine).
     *
     * @return the {@code kid} of the new key.
     */
    String rotate();

    /**
     * Flags a key {@code decommissioned}: it stops being used for signing and stops verifying, so
     * tokens carrying this {@code kid} no longer work. Idempotent.
     *
     * @throws IllegalStateException if it is the last live signing key - rotate first.
     */
    void decommission( String keyId );
}
