describe("api.util.DateHelperTest", function () {

    describe("when parseUTCDateTime", function () {

        it("given a string '2000-05-23T16:45:15' then returned Date is correct", function () {

            var date = api.util.DateHelper.parseUTCDateTime("2000-05-23T16:45:15");
            expect(date.getUTCFullYear()).toBe(2000);
            expect(date.getUTCMonth()).toBe(4);
            expect(date.getUTCDate()).toBe(23);
            expect(date.getUTCHours()).toBe(16);
            expect(date.getUTCMinutes()).toBe(45);
            expect(date.getUTCSeconds()).toBe(15);
        });

        it("given a string '2000-05-23T00:01:00' then returned Date is correct", function () {

            var date = api.util.DateHelper.parseUTCDateTime("2000-05-23T00:01:00");
            expect(date.getUTCFullYear()).toBe(2000);
            expect(date.getUTCMonth()).toBe(4);
            expect(date.getUTCDate()).toBe(23);
            expect(date.getUTCHours()).toBe(0);
            expect(date.getUTCMinutes()).toBe(1);
            expect(date.getUTCSeconds()).toBe(0);
        });

        it("given a string '2000-05-23T23:59:59' then returned Date is correct", function () {

            var date = api.util.DateHelper.parseUTCDateTime("2000-05-23T23:59:59");
            expect(date.getUTCFullYear()).toBe(2000);
            expect(date.getUTCMonth()).toBe(4);
            expect(date.getUTCDate()).toBe(23);
            expect(date.getUTCHours()).toBe(23);
            expect(date.getUTCMinutes()).toBe(59);
            expect(date.getUTCSeconds()).toBe(59);
        });
    });

    describe("when parseUTCDate", function () {

        it("given a string '2014-09-15' then returned Date is correct", function () {

            var date = api.util.DateHelper.parseUTCDate("2014-09-15");
            expect(date.getUTCFullYear()).toBe(2014);
            expect(date.getUTCMonth()).toBe(8);
            expect(date.getUTCDate()).toBe(15);
        });

    });

    describe("when parseUTCTime", function () {

        it("given a string with current local time then returned utc time is correct", function () {
            var now = new Date();
            var timeAsString = '' + now.getHours() + ':' + now.getMinutes();
            var timeUTC = api.util.DateHelper.parseTimeToUTC(timeAsString);
            var expectedTime = now.getUTCHours() + ':' + now.getUTCMinutes();
            expect(timeUTC).toEqual(expectedTime);

        });

        it("given a string '00:59' then returned utc time  is correct", function () {
            var date = new Date();
            date.setHours(0);
            date.setMinutes(59);
            var timeAsString = '' + date.getHours() + ':' + date.getMinutes();
            var timeUTC = api.util.DateHelper.parseTimeToUTC(timeAsString);
            var expectedTime = date.getUTCHours() + ':' + date.getUTCMinutes();
            expect(timeUTC).toEqual(expectedTime);

        });

    });

    describe("when formatUTCDate", function () {

        it("given a string with a date then returned utc date is correct", function () {
            var now = new Date(2014, 08, 15, 22, 30);
            var dateAsString = api.util.DateHelper.formatUTCDate(now);
            var actualDate = new Date(Date.parse(dateAsString));
            expect(now.getFullYear() == actualDate.getFullYear() && now.getMonth() == actualDate.getMonth() &&
                   now.getDay() == actualDate.getDay()).toBeTruthy();

        });
    });


    describe("when formatUTCDateTime", function () {

        it("given a date as string when string parsed then returned utc date is correct", function () {
            var date = new Date(2014, 08, 15, 22, 30);
            var dateAsString = api.util.DateHelper.formatUTCDateTime(date);
            var number = Date.parse(dateAsString);
            var dateFromParsedString = new Date(number);
            expect((date - dateFromParsedString) == 0).toBeTruthy();
        });

    });

    describe("when newUTCDateTime", function () {

        it("given xxx", function () {

            var date = api.util.DateHelper.newUTCDateTime(2000, 0, 1, 12, 30);
            var time = date.getTime();
            expect(time).not.toBe(isNaN(time));
        });


    });


});