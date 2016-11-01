describe("api.data.type.LocalDateTimeValueType", () => {

    describe("when isValid", () => {

        it("given a valid date time as string then true is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.isValid("2000-01-01T12:30:00")).toBe(true);
        });

        it("given a letter as string then false is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.isValid("a")).toBe(false);
        });

        it("given an empty string then false is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.isValid("")).toBe(false);
        });
    });

    describe("when isConvertible", () => {

        it("given a date time as string then true is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.isConvertible("2000-01-01T12:30:00")).toBeTruthy();
        });

        it("given a partly date time as string then true is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.isConvertible("2000-01-01T12")).toBeFalsy();
        });

        it("given a letter as string then false is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.isConvertible("a")).toBeFalsy();
        });

        it("given an empty string then false is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.isConvertible("")).toBeFalsy();
        });

        it("given an blank string then false is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.isConvertible(" ")).toBeFalsy();
        });
    });

    describe("when newValue", () => {

        it("given invalid date time string '2000-01-01T12' then a null is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.newValue("2000-01-01T12")).toEqual(new Value(null, ValueTypes.LOCAL_DATE_TIME));
        });

        it("given an empty string then a null is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.newValue("")).toEqual(new Value(null, ValueTypes.LOCAL_DATE_TIME));
        });
    });

    describe("when toJsonValue", () => {

        it("given null then null is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.toJsonValue(new Value(null, ValueTypes.LOCAL_DATE_TIME))).toBeNull();
        });

        it("given date time string '2000-01-01T12:30:00' then an equal date string is returned", () => {
            expect(ValueTypes.LOCAL_DATE_TIME.toJsonValue(ValueTypes.LOCAL_DATE_TIME.newValue("2000-01-01T12:30:00"))).toEqual("2000-01-01T12:30:00");
        });
    });

});