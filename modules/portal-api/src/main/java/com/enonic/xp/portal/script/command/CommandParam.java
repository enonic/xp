package com.enonic.xp.portal.script.command;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

// TODO: Add json(..) that will convert using jackson
public interface CommandParam
{
    public CommandParam required();

    public <T> T value( Class<T> type );

    public <T> T value( Class<T> type, T defValue );

    public Function<Object[], Object> callback();

    public Map<String, Object> map();

    public <T> List<T> array( Class<T> type );

    // public <T> T bean(Class<T> type);
}
