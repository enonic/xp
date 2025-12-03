package com.enonic.xp.impl.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.form.Form;
import com.enonic.xp.task.TaskDescriptor;

abstract class TaskDescriptorBuilderMapper
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
}
