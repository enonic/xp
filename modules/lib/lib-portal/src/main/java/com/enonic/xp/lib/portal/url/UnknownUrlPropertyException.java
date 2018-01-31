package com.enonic.xp.lib.portal.url;

public class UnknownUrlPropertyException
        extends RuntimeException {

    public UnknownUrlPropertyException(final String paramKey) {
        super("Unknown property: " + paramKey);
    }

}
