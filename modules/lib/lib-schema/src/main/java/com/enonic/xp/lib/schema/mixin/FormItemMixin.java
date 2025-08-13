package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface FormItemMixin
{
}
