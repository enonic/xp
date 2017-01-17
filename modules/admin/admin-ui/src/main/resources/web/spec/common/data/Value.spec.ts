describe('api.data.Value', () => {

    describe('when new', () => {

        it('given a letter as value and ValueType is LONG then Error is thrown', () => {

            expect(() => {
                // tslint:disable-next-line:no-unused-new
                new Value('a', ValueTypes.LONG);

            }).toThrow(new Error('Invalid value for type Long: a'));
        });

        it('given a number as value and ValueType is LONG then new instance is created', () => {

            expect(new Value(1, ValueTypes.LONG)).not.toBe(null);
        });
    });

    describe('when isNull', () => {

        it('given a empty string as value and ValueType is STRING then false is returned', () => {

            expect(new Value('', ValueTypes.STRING).isNull()).toBeFalsy();
        });
    });

    describe('when isNotNull', () => {

        it('given a empty string as value and ValueType is STRING then true is returned', () => {

            expect(new Value('', ValueTypes.STRING).isNotNull()).toBeTruthy();
        });
    });

    describe('when getDate', () => {

        it('given a number as value and ValueType is LONG then new instance is created', () => {

            expect(new Value(1, ValueTypes.LONG)).not.toBe(null);
        });
    });
});
