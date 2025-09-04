package com.enonic.xp.impl.task;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.task.TaskDescriptor;

final class YmlTaskDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( TaskDescriptor.Builder.class, TaskDescriptorBuilderMapper.class );
    }

    public static TaskDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, TaskDescriptor.Builder.class, currentApplication );
    }
}
