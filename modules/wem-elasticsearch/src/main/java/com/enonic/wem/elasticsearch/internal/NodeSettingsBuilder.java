package com.enonic.wem.elasticsearch.internal;

import org.elasticsearch.common.settings.Settings;

interface NodeSettingsBuilder
{
    public Settings buildSettings();
}
