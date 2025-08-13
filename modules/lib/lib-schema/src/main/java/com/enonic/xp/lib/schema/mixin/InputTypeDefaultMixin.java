package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.inputtype.InputTypeDefault;

@JsonDeserialize(builder = InputTypeDefault.Builder.class)
public abstract class InputTypeDefaultMixin
{
}
