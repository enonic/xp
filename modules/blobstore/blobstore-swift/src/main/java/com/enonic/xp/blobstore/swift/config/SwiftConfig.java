package com.enonic.xp.blobstore.swift.config;

import com.enonic.xp.blob.ProviderConfig;

public interface SwiftConfig
    extends ProviderConfig
{
    String authUrl();

    String domainId();

    String domainName();

    String authUser();

    String authPassword();

    Integer authVersion();

    String projectId();

    public String projectName();
}
