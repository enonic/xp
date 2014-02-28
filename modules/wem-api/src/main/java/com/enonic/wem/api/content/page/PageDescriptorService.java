package com.enonic.wem.api.content.page;

public interface PageDescriptorService
{
    PageDescriptor getByKey( final PageDescriptorKey key );

    PageDescriptor create( final CreatePageDescriptorParams params );
}
