package com.enonic.xp.security;

import javax.crypto.SecretKey;

/**
 * Resolves managed signing keys for self-issued tokens (e.g. device-login access tokens).
 * <p>
 * Key material is generated once and stored in the system repository (so it is shared
 * across the cluster). The <i>effective</i> key returned by this service is derived from
 * the stored material and the optional {@code encryption_key} configured for the security
 * bundle:
 * <ul>
 *     <li>When no encryption key is configured, the stored material is used directly
 *     (backwards compatible).</li>
 *     <li>When an encryption key is configured, the effective key is derived from it, so
 *     two environments that share identical stored material (e.g. a production repository
 *     copied to QA) still end up with different effective keys. Tokens therefore never
 *     work across environments, while each environment keeps working without any
 *     regeneration step.</li>
 * </ul>
 * The stored material on its own is consequently not sufficient to forge or verify a token.
 */
public interface CryptoService
{
    /**
     * The key id ({@code kid}) of the default key used to sign self-issued access tokens.
     */
    String tokenSigningKeyId();

    /**
     * Resolves the effective signing/verification key for the given key id.
     *
     * @param kid the key id, as carried in a token's JOSE {@code kid} header. It must
     *            reference a known signing key; arbitrary node paths are rejected.
     * @return the derived secret key (an {@code HmacSHA512} key).
     * @throws IllegalArgumentException if the key id is not a known signing key.
     */
    SecretKey getSigningKey( String kid );
}
