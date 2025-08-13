package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.form.Input;

@JsonDeserialize(builder = Input.Builder.class)
public abstract class InputMixin
{
}
