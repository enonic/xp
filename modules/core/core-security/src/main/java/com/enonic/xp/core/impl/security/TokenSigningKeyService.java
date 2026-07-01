package com.enonic.xp.core.impl.security;

import javax.crypto.SecretKey;

import org.jspecify.annotations.NullMarked;

/**
 * Resolves the symmetric signing keys used for self-issued access tokens (e.g. device-login tokens)
 * from a PKCS12 keystore configured for the security bundle. This is an internal collaborator of
 * core-security - it is deliberately <b>not</b> part of the public security API, because it deals in
 * raw key material ({@link SecretKey}) that must not leak onto {@code SecurityService}.
 * <p>
 * The keystore holds one or more {@code HmacSHA512} secret-key entries; each entry's alias is its
 * {@code kid}. Any entry verifies; the configured signing alias signs new tokens. Key lifecycle is
 * the operator's responsibility, managed with {@code keytool} on the keystore: rotate by adding an
 * entry and repointing the signing alias, retire one by removing it - so there is no in-process key
 * management here.
 */
@NullMarked
public interface TokenSigningKeyService
{
    /**
     * @return the {@code kid} (keystore alias) used to sign new tokens.
     * @throws IllegalStateException if no signing alias is configured or it is not in the keystore.
     */
    String getCurrentKeyId();

    /**
     * Resolves the verification/signing key for the given key id.
     *
     * @param kid the key id, as carried in a token's JOSE {@code kid} header. It must reference a
     *            keystore alias holding a secret key; arbitrary values are rejected.
     * @return the {@code HmacSHA512} secret key.
     * @throws IllegalArgumentException if the key id is not a known keystore entry.
     */
    SecretKey getSigningKey( String kid );
}
