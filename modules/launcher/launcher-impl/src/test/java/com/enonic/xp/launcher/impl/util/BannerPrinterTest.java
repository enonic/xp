package com.enonic.xp.launcher.impl.util;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.launcher.impl.VersionInfo;
import com.enonic.xp.launcher.impl.env.Environment;

class BannerPrinterTest
{
    @Test
    void printBanner()
    {
        final Environment env = Mockito.mock( Environment.class );
        final VersionInfo info = VersionInfo.get();

        final BannerPrinter printer = new BannerPrinter( env, info );
        printer.printBanner();
    }
}
