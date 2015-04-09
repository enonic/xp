package com.enonic.xp.portal.script.command;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.annotations.Beta;

@Beta
public interface CommandParam
{
    CommandParam required();

    <T> T value( Class<T> type );

    <T> T value( Class<T> type, T defValue );

    Function<Object[], Object> callback();

    Map<String, Object> map();

    <T> List<T> array( Class<T> type );
}
