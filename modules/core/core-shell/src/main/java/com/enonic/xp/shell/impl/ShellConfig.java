package com.enonic.xp.shell.impl;

public @interface ShellConfig
{
    boolean enabled() default false;

    String telnet_ip() default "127.0.0.1";

    int telnet_port() default 5555;

    int telnet_maxConnect() default 2;

    int telnet_socketTimeout() default 0;
}
