package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;

public interface InputFactory<T extends InputYml>
{
    Input build( T yml );
}
