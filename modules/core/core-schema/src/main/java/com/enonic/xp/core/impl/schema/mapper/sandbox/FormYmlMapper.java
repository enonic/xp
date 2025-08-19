package com.enonic.xp.core.impl.schema.mapper.sandbox;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FormYml.FormYmlDeserializer.class)
public abstract class FormYmlMapper
{
}
