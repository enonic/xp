describe("api.util.LocalTime", () => {

    var localTime;

    describe("basic asserts", () => {

        beforeEach(() => {
            localTime = api.util.LocalTime.create().setHours(12).setMinutes(5).build();
        });

        it("should create an instance", () => {
            expect(localTime).toBeDefined();
        });

        it("getHours() should return correct hours", () => {
            expect(localTime.getHours()).toEqual(12);
        });

        it("getMinutes() should return correct minutes", () => {
            expect(localTime.getMinutes()).toEqual(5);
        });

        it("getSeconds() should return 0 when seconds are not passed to constructor", () => {
            expect(localTime.getSeconds()).toEqual(0);
        });

        it("getSeconds() should return correct seconds when passed to constructor", () => {
            localTime = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(37).build();

            expect(localTime.getSeconds()).toEqual(37);
        });

    });

    describe("conversion to string", () => {

        it("should correctly convert time without seconds", () => {
            localTime = api.util.LocalTime.create().setHours(12).setMinutes(5).build();

            expect(localTime.toString()).toEqual("12:05");
        });

        it("should correctly convert time with seconds", () => {
            localTime = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(7).build();

            expect(localTime.toString()).toEqual("12:05:07");
        });
    });

    describe("comparison", () => {

        it("should correctly compare equal times with seconds", () => {
            var time1 = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(7).build();
            var time2 = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(7).build();

            expect(time1.equals(time2)).toBeTruthy();
        });

        it("should correctly compare equal times without seconds", () => {
            var time1 = api.util.LocalTime.create().setHours(12).setMinutes(5).build();
            var time2 = api.util.LocalTime.create().setHours(12).setMinutes(5).build();

            expect(time1.equals(time2)).toBeTruthy();
        });

        it("should correctly compare unequal times", () => {
            var time1 = api.util.LocalTime.create().setHours(12).setMinutes(5).build();
            var time2 = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(1).build();

            expect(time1.equals(time2)).toBeFalsy();
        });

        it("should correctly compare equal times with empty and zero seconds", () => {
            var time1 = api.util.LocalTime.create().setHours(12).setMinutes(5).build();
            var time2 = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(0).build();

            expect(time1.equals(time2)).toBeTruthy();
        });
    });


    describe("parsing of a time literal", () => {

        it("should not parse empty string", () => {
            expect(function () {
                api.util.LocalTime.fromString("");
            }).toThrow();
        });

        it("should not parse value that is not time", () => {
            expect(function () {
                api.util.LocalTime.fromString("this is not time");
            }).toThrow();
        });

        it("should not parse time with incorrect separators", () => {
            expect(function () {
                api.util.LocalTime.fromString("12.05.37");
            }).toThrow();
        });

        it("should not parse incorrect time", () => {
            expect(function () {
                api.util.LocalTime.fromString("24:05");
            }).toThrow();
        });

        it("should parse time in correct format", () => {
            var parsedTime = api.util.LocalTime.fromString("12:05:37");
            var originalTime = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(37).build();

            expect(originalTime.equals(parsedTime)).toBeTruthy();
        });

        it("should parse time in correct format", () => {
            var parsedTime = api.util.LocalTime.fromString("12");
            var originalTime = api.util.LocalTime.create().setHours(12).setMinutes(0).setSeconds(0).build();

            expect(originalTime.equals(parsedTime)).toBeTruthy();
        });

        it("should parse time in correct format", () => {
            var parsedTime = api.util.LocalTime.fromString("6:7");
            var originalTime = api.util.LocalTime.create().setHours(6).setMinutes(7).setSeconds(0).build();
            expect(originalTime.equals(parsedTime)).toBeTruthy();
        });

    });
});