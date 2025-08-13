package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.form.Form;

@JsonDeserialize(builder = Form.Builder.class)
public abstract class FormMixin
{
}
