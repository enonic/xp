package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FormDeserializer.class)
public abstract class FormMapper
{
}
