package com.enonic.wem.core.exporters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * filename for to directory/zip export or import
 */
@Retention( RetentionPolicy.RUNTIME )
public @interface XMLFilename
{
    String value();
}
