package com.enonic.xp.app;

import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface VirtualAppService
{
//     void create( CreateVirtualAppParams params );

    List<Application> list();

    Application get( ApplicationKey applicationKey );
}
