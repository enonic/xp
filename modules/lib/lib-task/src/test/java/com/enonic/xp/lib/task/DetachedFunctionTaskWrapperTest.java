package com.enonic.xp.lib.task;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DetachedFunctionTaskWrapperTest
{
    @Test
    void failsFastWithoutScriptService()
    {
        final DetachedFunctionTaskWrapper wrapper =
            new DetachedFunctionTaskWrapper( () -> null, ResourceKey.from( "myapp:" + DetachedFunctionTaskWrapper.RUNNER_PATH ),
                                             "function () {}", null, "description" );

        assertThrows( IllegalStateException.class, () -> wrapper.run( TaskId.from( "123" ), mock( ProgressReporter.class ) ) );
    }
}
