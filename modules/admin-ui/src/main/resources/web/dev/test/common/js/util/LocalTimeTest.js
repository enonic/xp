describe("api.util.LocalTimeTest", function () {

    var localTime;

    describe("basic asserts", function () {

        beforeEach(function () {
            localTime = api.util.LocalTime.create().setHours(12).setMinutes(5).build();
        });

        it("should create an instance", function () {
            expect(localTime).toBeDefined();
        });

        it("getHours() should return correct hours", function () {
            expect(localTime.getHours()).toEqual(12);
        });

        it("getMinutes() should return correct minutes", function () {
            expect(localTime.getMinutes()).toEqual(5);
        });

        it("getSeconds() should return 0 when seconds are not passed to constructor", function () {
            expect(localTime.getSeconds()).toEqual(0);
        });

        it("getSeconds() should return correct seconds when passed to constructor", function () {
            localTime = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(37).build();

            expect(localTime.getSeconds()).toEqual(37);
        });

    });

    describe("conversion to string", function () {

        it("should correctly convert time without seconds", function () {
            localTime = api.util.LocalTime.create().setHours(12).setMinutes(5).build();

            expect(localTime.toString()).toEqual("12:05");
        });

        it("should correctly convert time with seconds", function () {
            localTime = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(7).build();

            expect(localTime.toString()).toEqual("12:05:07");
        });
    });

    describe("comparison", function () {

        it("should correctly compare equal times with seconds", function () {
            var time1 = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(7).build();
            var time2 = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(7).build();

            expect(time1.equals(time2)).toBeTruthy();
        });

        it("should correctly compare equal times without seconds", function () {
            var time1 = api.util.LocalTime.create().setHours(12).setMinutes(5).build();
            var time2 = api.util.LocalTime.create().setHours(12).setMinutes(5).build();

            expect(time1.equals(time2)).toBeTruthy();
        });

        it("should correctly compare unequal times", function () {
            var time1 = api.util.LocalTime.create().setHours(12).setMinutes(5).build();
            var time2 = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(1).build();

            expect(time1.equals(time2)).toBeFalsy();
        });

        it("should correctly compare equal times with empty and zero seconds", function () {
            var time1 = api.util.LocalTime.create().setHours(12).setMinutes(5).build();
            var time2 = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(0).build();

            expect(time1.equals(time2)).toBeTruthy();
        });
    });


    describe("parsing of a time literal", function () {

        it("should not parse empty string", function () {
            expect(function() {
                api.util.LocalTime.fromString("");
            }).toThrow();
        });

        it("should not parse value that is not time", function () {
            expect(function() {
                api.util.LocalTime.fromString("this is not time");
            }).toThrow();
        });

        it("should not parse time without hours", function () {
            expect(function() {
                api.util.LocalTime.fromString("12");
            }).toThrow();
        });

        it("should not parse time with incorrect separators", function () {
            expect(function() {
                api.util.LocalTime.fromString("12.05.37");
            }).toThrow();
        });

        it("should not parse incorrect time", function () {
            expect(function() {
                api.util.LocalTime.fromString("24:05");
            }).toThrow();
        });

        it("should parse time in correct format", function () {
            var parsedTime = api.util.LocalTime.fromString("12:05:37");
            var originalTime = api.util.LocalTime.create().setHours(12).setMinutes(5).setSeconds(37).build();

            expect(originalTime.equals(parsedTime)).toBeTruthy();
        });

    });
});