describe('api.data.type.GeoPointValueTypeTest', () => {

    describe('when isValid', () => {

        it("given geo point '1.1,-2.2' then true is returned", () => {
            expect(ValueTypes.GEO_POINT.isValid(GeoPoint.fromString('1.1,-2.2'))).toBe(true);
        });

        it("given geo point '1,2' then true is returned", () => {
            expect(ValueTypes.GEO_POINT.isValid(GeoPoint.fromString('2,2'))).toBe(true);
        });

        it('given a number then false is returned', () => {
            expect(ValueTypes.GEO_POINT.isValid(1.1)).toBe(false);
        });

        it('given a letter as string then false is returned', () => {
            expect(ValueTypes.GEO_POINT.isValid('a')).toBe(false);
        });

        it('given an empty string then false is returned', () => {
            expect(ValueTypes.GEO_POINT.isValid('')).toBe(false);
        });
    });

    describe('when isConvertible', () => {

        it('given a geo point string then true is returned', () => {
            expect(ValueTypes.GEO_POINT.isConvertible('1.1,-2.2')).toBeTruthy();
        });

        it('given a letter as string then false is returned', () => {
            expect(ValueTypes.GEO_POINT.isConvertible('a')).toBeFalsy();
        });

        it('given an empty string then false is returned', () => {
            expect(ValueTypes.GEO_POINT.isConvertible('')).toBeFalsy();
        });

        it('given an blank string then false is returned', () => {
            expect(ValueTypes.GEO_POINT.isConvertible(' ')).toBeFalsy();
        });
    });

    describe('when newValue', () => {

        it('given a geo point as string then a new Value is returned', () => {
            let actual = ValueTypes.GEO_POINT.newValue('1.1,-2.2');
            let expected = new Value(GeoPoint.fromString('1.1,-2.2'), ValueTypes.GEO_POINT);
            expect(actual).toEqual(expected);
        });

        it('given an empty string then a Value with null is returned', () => {
            expect(ValueTypes.GEO_POINT.newValue('')).toEqual(new Value(null, ValueTypes.GEO_POINT));
        });
    });

});
