package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.inputtype.InputTypeProperty;

@JsonDeserialize(builder = InputTypeProperty.Builder.class)
public abstract class InputTypePropertyMixin
{
}
