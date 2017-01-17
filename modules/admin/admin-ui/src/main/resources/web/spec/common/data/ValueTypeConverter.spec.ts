import ValueTypeConverter = api.data.ValueTypeConverter;

describe('api.data.ValueTypeConverter', () => {

    describe('to string conversion test', () => {

        it('number value should be converted to string', () => {
            let convertableNumberValue = new Value(1, ValueTypes.LONG);
            let converted = ValueTypeConverter.convertTo(convertableNumberValue, ValueTypes.STRING);

            expect(converted.getString()).toBe('1');
        });

        it('localdatetime value should be converted to string', () => {
            let convertableDateValue = ValueTypes.LOCAL_DATE_TIME.newValue('2011-04-11T11:51:00');
            let converted = ValueTypeConverter.convertTo(convertableDateValue, ValueTypes.STRING);

            expect(converted.getString()).not.toBeNull();
        });
    });

    describe('to data conversion test', () => {

        it('string value should be converted to data', () => {
            let convertableValue = new Value('XxX', ValueTypes.STRING);
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DATA);

            expect(converted.getPropertySet().isDetached()).toBeTruthy();
            expect(converted.getObject()).not.toBeNull();
        });
    });

    describe('to xml conversion test', () => {

        it('string value should be converted to xml', () => {
            let convertableValue = new Value('XxX', ValueTypes.STRING);
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.XML);

            expect(converted.getString()).toBe('XxX');
        });
        ;
    });

    describe('to local date conversion test', () => {

        it('string value should be converted to local date', () => {
            let convertableValue = new Value('2015-01-01', ValueTypes.STRING);
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE);

            expect(converted.getString()).toBe('2015-01-01');
        });

        it('localdatetime value should be converted to local date', () => {
            let convertableValue = ValueTypes.LOCAL_DATE_TIME.newValue('2011-04-11T00:00:00');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE);

            expect(converted.getString()).toBe('2011-04-11');
        });

        it('datetime value should be converted to local date', () => {
            let convertableValue = ValueTypes.DATE_TIME.newValue('2011-04-11T00:00:00');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE);

            expect(converted.getString()).toBe('2011-04-11');
        });

        it('bad value value should be converted to null local date', () => {
            let convertableValue = ValueTypes.STRING.newValue('bad');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE);

            expect(converted.getObject()).toBeNull();
        });
    });

    describe('to local time conversion test', () => {

        it('string value should be converted to local time', () => {
            let convertableValue = new Value('20:00', ValueTypes.STRING);
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_TIME);

            expect(converted.getString()).toBe('20:00');
        });

        it('localdatetime value should be converted to local time', () => {
            let convertableValue = ValueTypes.LOCAL_DATE_TIME.newValue('2011-04-11T11:51:00');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_TIME);

            expect(converted.getString()).toBe('11:51');
        });

        it('datetime value should be converted to local time', () => {
            let convertableValue = ValueTypes.DATE_TIME.newValue('2011-04-11T11:51:00');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_TIME);

            expect(converted.getString()).toBe('11:51');
        });

        it('bad value value should be converted to null local time', () => {
            let convertableValue = ValueTypes.STRING.newValue('bad');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_TIME);

            expect(converted.getObject()).toBeNull();
        });
    });

    describe('to local datetime conversion test', () => {

        it('string value should be converted to local datetime', () => {
            let convertableValue = new Value('2015-01-01T15:00:01', ValueTypes.STRING);
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE_TIME);

            expect(converted.getString()).toBe('2015-01-01T15:00:01');
        });

        it('localdate value should be converted to local datetime', () => {
            let convertableValue = ValueTypes.LOCAL_DATE.newValue('2011-04-11');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE_TIME);

            expect(converted.getString()).toBe('2011-04-11T00:00:00');
        });

        it('bad localdate value should be converted to local datetime', () => {
            let convertableValue = ValueTypes.LOCAL_DATE.newValue('bad');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE_TIME);

            expect(converted.getString()).toBeNull();
        });

        it('datetime value should be converted to local datetime', () => {
            let convertableValue = ValueTypes.DATE_TIME.newValue('2011-04-11T11:51:00');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE_TIME);

            expect(converted.getString()).not.toBeNull();
        });

        it('bad value value should be converted to null local datetime', () => {
            let convertableValue = ValueTypes.STRING.newValue('bad');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LOCAL_DATE_TIME);

            expect(converted.getObject()).toBeNull();
        });
    });

    describe('to datetime conversion test', () => {

        it('string value should be converted to datetime', () => {
            let convertableValue = new Value('2015-01-01T15:00:01', ValueTypes.STRING);
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DATE_TIME);

            expect(converted.getString()).toBe('2015-01-01T15:00:01+00:00');
        });

        it('localdate value should be converted to datetime', () => {
            let convertableValue = ValueTypes.LOCAL_DATE.newValue('2011-04-11');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DATE_TIME);

            expect(converted.getString()).toBe('2011-04-11T00:00:00+00:00');
        });

        it('local datetime value should be converted to datetime', () => {
            let convertableValue = ValueTypes.LOCAL_DATE_TIME.newValue('2011-04-11T11:51:00');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DATE_TIME);

            expect(converted.getString()).toBe('2011-04-11T11:51:00+00:00');
        });

        it('bad value value should be converted to null datetime', () => {
            let convertableValue = ValueTypes.STRING.newValue('bad');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DATE_TIME);

            expect(converted.getObject()).toBeNull();
        });
    });

    describe('to long conversion test', () => {

        it('string value should be converted to long', () => {
            let convertableValue = new Value('100', ValueTypes.STRING);
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LONG);

            expect(converted.getString()).toBe('100');
        });

        it('double value should be converted to long', () => {
            let convertableValue = ValueTypes.DOUBLE.newValue('200.00');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LONG);

            expect(converted.getString()).toBe('200');
        });

        it('boolean value should be converted to long', () => {
            let convertableValue = ValueTypes.BOOLEAN.newValue('true');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LONG);

            expect(converted.getString()).toBe('1');
        });

        it('bad value value should be converted to null long', () => {
            let convertableValue = ValueTypes.STRING.newValue('bad');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.LONG);

            expect(converted.getObject()).toBeNull();
        });
    });

    describe('to double conversion test', () => {

        it('string value should be converted to double', () => {
            let convertableValue = new Value('100', ValueTypes.STRING);
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DOUBLE);

            expect(converted.getString()).toBe('100');
        });

        it('long value should be converted to double', () => {
            let convertableValue = ValueTypes.LONG.newValue('200');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DOUBLE);

            expect(converted.getString()).toBe('200');
        });

        it('boolean value should be converted to long', () => {
            let convertableValue = ValueTypes.BOOLEAN.newValue('true');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DOUBLE);

            expect(converted.getString()).toBe('1');
        });

        it('bad value value should be converted to null long', () => {
            let convertableValue = ValueTypes.STRING.newValue('bad');
            let converted = ValueTypeConverter.convertTo(convertableValue, ValueTypes.DOUBLE);

            expect(converted.getObject()).toBeNull();
        });
    });
});
