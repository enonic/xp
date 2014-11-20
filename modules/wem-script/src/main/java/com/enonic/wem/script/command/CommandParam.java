package com.enonic.wem.script.command;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface CommandParam
{
    public CommandParam required();

    public <T> T value( Class<T> type );

    public <T> T value( Class<T> type, T defValue );

    public Function<Object, Object> function();

    public Map<String, Object> map();

    public <T> List<T> array( Class<T> type );
}
