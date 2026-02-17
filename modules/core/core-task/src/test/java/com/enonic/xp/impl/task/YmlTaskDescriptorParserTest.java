package com.enonic.xp.impl.task;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.task.TaskDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YmlTaskDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yml = readAsString( "/descriptors/task-descriptor.yml" );

        final ApplicationKey myapp = ApplicationKey.from( "myapp" );

        final TaskDescriptor.Builder descriptorBuilder = YmlTaskDescriptorParser.parse( yml, myapp );
        final DescriptorKey descriptorKey = DescriptorKey.from( myapp, "vacuum" );
        descriptorBuilder.key( descriptorKey );

        final TaskDescriptor descriptor = descriptorBuilder.build();
        assertNotNull( descriptor );
        assertEquals( descriptorKey, descriptor.getKey() );
        assertEquals( "Vacuum task", descriptor.getDescription() );

        final Form config = descriptor.getConfig();

        final Input ageThresholdField = config.getFormItem( FormItemPath.from( "ageThreshold" ) ).toInput();
        assertEquals( InputTypeName.TEXT_LINE, ageThresholdField.getInputType() );
        assertEquals( "ageThreshold", ageThresholdField.getName() );
        assertEquals( "Age threshold", ageThresholdField.getLabel() );
        assertEquals( 0, ageThresholdField.getOccurrences().getMinimum() );
        assertEquals( 1, ageThresholdField.getOccurrences().getMaximum() );

        final Input tasksField = config.getFormItem( FormItemPath.from( "tasks" ) ).toInput();
        assertEquals( InputTypeName.TEXT_LINE, tasksField.getInputType() );
        assertEquals( "tasks", tasksField.getName() );
        assertEquals( "Tasks to be run", tasksField.getLabel() );
        assertEquals( 0, tasksField.getOccurrences().getMinimum() );
        assertEquals( 0, tasksField.getOccurrences().getMaximum() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlTaskDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
