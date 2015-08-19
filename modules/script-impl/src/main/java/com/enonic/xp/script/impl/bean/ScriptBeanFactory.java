package com.enonic.xp.script.impl.bean;

public interface ScriptBeanFactory
{
    Object newBean( String type )
        throws Exception;
}
