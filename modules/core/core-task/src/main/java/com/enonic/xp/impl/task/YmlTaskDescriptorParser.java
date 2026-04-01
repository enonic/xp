package com.enonic.xp.impl.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.util.GenericValue;

final class YmlTaskDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( TaskDescriptor.Builder.class, TaskDescriptorBuilderMapper.class );
    }

    public static TaskDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( "Task", resource, TaskDescriptor.Builder.class, currentApplication );
    }

    @JsonIgnoreProperties("kind")
    private abstract static class TaskDescriptorBuilderMapper
    {
        @JsonCreator
        static TaskDescriptor.Builder create()
        {
            return TaskDescriptor.create();
        }

        @JsonProperty("description")
        abstract TaskDescriptor.Builder description( String description );

        @JsonProperty("form")
        abstract TaskDescriptor.Builder config( Form config );

        @JsonProperty("config")
        abstract TaskDescriptor.Builder schemaConfig( GenericValue schemaConfig );
    }
}
