package com.enonic.xp.script.graaljs.impl.bean;

public interface ScriptBeanFactory
{
    Object newBean( String type )
        throws Exception;
}
