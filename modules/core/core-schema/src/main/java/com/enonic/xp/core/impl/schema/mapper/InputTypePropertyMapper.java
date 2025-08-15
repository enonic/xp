package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = InputTypePropertyBuilderDeserializer.class)
public abstract class InputTypePropertyMapper
{
}
