package com.enonic.xp.security;

import org.jspecify.annotations.NullMarked;

/**
 * Manages the lifecycle of the token-signing keys used for self-issued access tokens.
 * <p>
 * Keys are stored flat under {@code /keys}; each node's name is its {@code kid} and a
 * {@code preferred} field marks the single key currently used for <i>signing</i>. All present
 * keys remain valid for <i>verification</i>, which supports rotation with an overlap window:
 * <ol>
 *     <li><b>Rotate</b> – {@link #rotate()} creates a new key, marks it preferred and demotes the
 *     previous one. New tokens are signed with the new key; tokens signed by the previous key keep
 *     verifying (it is now verify-only).</li>
 *     <li><b>Decommission</b> – once no live token references a verify-only key (i.e. after the
 *     access-token TTL has elapsed), {@link #decommission(String)} deletes it; tokens carrying that
 *     {@code kid} then fail verification.</li>
 * </ol>
 */
@NullMarked
public interface TokenSigningKeyManager
{
    /**
     * @return the {@code kid} of the key currently used for signing (the preferred key).
     */
    String getCurrentKeyId();

    /**
     * Generates a new signing key, marks it preferred and demotes the previous preferred key to
     * verify-only.
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
