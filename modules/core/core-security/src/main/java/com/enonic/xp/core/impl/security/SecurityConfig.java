package com.enonic.xp.core.impl.security;

public @interface SecurityConfig
{
    boolean auditlog_enabled() default true;

    String password_policy() default "$pbkdf2-sha512$i=210000,l=64,slen=16";

    /**
     * Path to the PKCS12 keystore holding the symmetric keys that sign/verify self-issued access
     * tokens (device / native login). When empty, token signing is unavailable.
     */
    String tokenSigningKeystore() default "";

    /**
     * Password protecting the token-signing keystore and its key entries.
     */
    String tokenSigningKeystorePassword() default "";

    /**
     * Alias of the keystore entry used to sign new tokens. Other entries remain valid for
     * verification; rotate by adding a new entry and repointing this alias, retire one by removing
     * it (with keytool) - so decommission and removal are the same thing, a change to the keystore.
     */
    String tokenSigningKeyAlias() default "";
}
