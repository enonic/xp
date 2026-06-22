package com.enonic.xp.core.impl.security;

public @interface SecurityConfig
{
    boolean auditlog_enabled() default true;

    String password_policy() default "$pbkdf2-sha512$i=210000,l=64,slen=16";

    /**
     * Optional key-encryption-key used to derive the effective signing keys for self-issued
     * tokens from the material stored in the system repository. When set, stored key material
     * copied between environments yields different effective keys, isolating tokens per
     * environment. When empty, stored material is used directly (backwards compatible).
     */
    String encryption_key() default "";
}
