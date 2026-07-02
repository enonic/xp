package com.enonic.xp.portal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PortalRequestAccessorTest
{
    @AfterEach
    void tearDown()
    {
        PortalRequestAccessor.remove();
    }

    @Test
    void callWithBindsRequestForScopeAndReturnsValue()
    {
        final PortalRequest request = new PortalRequest();

        final PortalRequest bound = PortalRequestAccessor.callWith( request, PortalRequestAccessor::get );

        assertThat( bound ).isSameAs( request );
        assertThat( PortalRequestAccessor.get() ).isNull();
    }

    @Test
    void runWithBindsRequestForScope()
    {
        final PortalRequest request = new PortalRequest();

        PortalRequestAccessor.runWith( request, () -> assertThat( PortalRequestAccessor.get() ).isSameAs( request ) );

        assertThat( PortalRequestAccessor.get() ).isNull();
    }

    @Test
    void nestedBindingRestoresOuterRequest()
    {
        final PortalRequest outer = new PortalRequest();
        final PortalRequest inner = new PortalRequest();

        PortalRequestAccessor.runWith( outer, () -> {
            assertThat( PortalRequestAccessor.get() ).isSameAs( outer );
            PortalRequestAccessor.runWith( inner, () -> assertThat( PortalRequestAccessor.get() ).isSameAs( inner ) );
            assertThat( PortalRequestAccessor.get() ).isSameAs( outer );
        } );

        assertThat( PortalRequestAccessor.get() ).isNull();
    }

    @Test
    void scopedBindingTakesPrecedenceOverLegacyAndDoesNotClobberIt()
    {
        final PortalRequest legacy = new PortalRequest();
        final PortalRequest scoped = new PortalRequest();

        PortalRequestAccessor.set( legacy );

        PortalRequestAccessor.runWith( scoped, () -> assertThat( PortalRequestAccessor.get() ).isSameAs( scoped ) );

        assertThat( PortalRequestAccessor.get() ).isSameAs( legacy );
    }

    @Test
    void legacySetAndRemove()
    {
        final PortalRequest request = new PortalRequest();

        assertThat( PortalRequestAccessor.get() ).isNull();

        PortalRequestAccessor.set( request );
        assertThat( PortalRequestAccessor.get() ).isSameAs( request );

        PortalRequestAccessor.remove();
        assertThat( PortalRequestAccessor.get() ).isNull();
    }
}
