package com.enonic.wem.admin.json;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableInstant;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.joda.deser.DateMidnightDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.IntervalDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.PeriodDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.DateMidnightSerializer;
import com.fasterxml.jackson.datatype.joda.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.joda.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.joda.ser.IntervalSerializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalTimeSerializer;

public class JodaModule
    extends SimpleModule
{

    public JodaModule()
    {
        // first deserializers 
        addDeserializer( DateMidnight.class, new DateMidnightDeserializer() );
        addDeserializer( DateTime.class, DateTimeDeserializer.forType( DateTime.class ) );
        addDeserializer( Duration.class, new DurationDeserializer() );
        addDeserializer( Instant.class, new InstantDeserializer() );
        addDeserializer( LocalDateTime.class, new LocalDateTimeDeserializer() );
        addDeserializer( LocalDate.class, new LocalDateDeserializer() );
        addDeserializer( LocalTime.class, new LocalTimeDeserializer() );
        addDeserializer( Period.class, new PeriodDeserializer() );
        addDeserializer( ReadableDateTime.class, DateTimeDeserializer.forType( ReadableDateTime.class ) );
        addDeserializer( ReadableInstant.class, DateTimeDeserializer.forType( ReadableInstant.class ) );
        addDeserializer( Interval.class, new IntervalDeserializer() );
        // then serializers:
        addSerializer( DateMidnight.class, new DateMidnightSerializer() );
        addSerializer( DateTime.class, new DateTimeSerializer() );
        addSerializer( Duration.class, new DurationSerializer() );
        addSerializer( Instant.class, new InstantSerializer() );
        addSerializer( LocalDateTime.class, new LocalDateTimeSerializer() );
        addSerializer( LocalDate.class, new LocalDateSerializer() );
        addSerializer( LocalTime.class, new LocalTimeSerializer() );
        addSerializer( Period.class, ToStringSerializer.instance );
        addSerializer( Interval.class, new IntervalSerializer() );
    }
}
