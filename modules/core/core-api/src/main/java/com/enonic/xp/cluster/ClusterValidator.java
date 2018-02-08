package com.enonic.xp.cluster;

public interface ClusterValidator
{
    ClusterValidatorResult validate( final ClusterProviders providers );
}
