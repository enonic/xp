package com.enonic.wem.itest;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "itest")
@ContextConfiguration("classpath:spring/itestContext.xml")
public abstract class AbstractSpringTest
{
}
