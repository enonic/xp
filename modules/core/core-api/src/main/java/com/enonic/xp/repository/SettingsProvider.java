package com.enonic.xp.repository;

import com.fasterxml.jackson.databind.JsonNode;

public interface SettingsProvider
{
    JsonNode get();
}
