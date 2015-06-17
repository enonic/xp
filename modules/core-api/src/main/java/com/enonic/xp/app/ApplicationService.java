package com.enonic.xp.app;

public interface ApplicationService
{

    Applications getAll();

    Application getByKey( ApplicationKey applicationKey );

}
