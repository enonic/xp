package com.enonic.xp.core.impl.schema.mapper.sandbox;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
public abstract class FormItemYmlMapper
{
}
