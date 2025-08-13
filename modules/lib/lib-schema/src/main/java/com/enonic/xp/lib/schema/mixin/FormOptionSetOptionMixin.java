package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.form.FormOptionSetOption;

@JsonDeserialize(builder = FormOptionSetOption.Builder.class)
public abstract class FormOptionSetOptionMixin
{
}
