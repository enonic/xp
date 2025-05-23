package com.enonic.xp.descriptor;

import com.enonic.xp.app.ApplicationKeys;

public interface DescriptorService
{
    interface Typed<T extends Descriptor>
    {
        T get(  DescriptorKey key );

       Descriptors<T> get(  DescriptorKeys keys );

        Descriptors<T> get(  ApplicationKeys keys );

        Descriptors<T> getAll(  );

        DescriptorKeys find(  ApplicationKeys keys );

         DescriptorKeys findAll(  );
    }

    <T extends Descriptor> T get( Class<T> type, DescriptorKey key );

    <T extends Descriptor> Descriptors<T> get( Class<T> type, DescriptorKeys keys );

    <T extends Descriptor> Descriptors<T> get( Class<T> type, ApplicationKeys keys );

    <T extends Descriptor> Descriptors<T> getAll( Class<T> type );

    <T extends Descriptor> DescriptorKeys find( Class<T> type, ApplicationKeys keys );

    <T extends Descriptor> DescriptorKeys findAll( Class<T> type );
}
