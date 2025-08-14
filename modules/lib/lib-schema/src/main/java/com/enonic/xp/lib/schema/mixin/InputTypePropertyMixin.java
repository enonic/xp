package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = InputTypePropertyBuilderDeserializer.class)
public abstract class InputTypePropertyMixin
{
}
