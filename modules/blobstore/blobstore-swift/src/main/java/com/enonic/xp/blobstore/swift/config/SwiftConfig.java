package com.enonic.xp.blobstore.swift.config;

import com.enonic.xp.blob.ProviderConfig;

public interface SwiftConfig
    extends ProviderConfig
{
    String container();

    String endpoint();

    String domain();

    String user();

    String password();

    String projectId();
}
