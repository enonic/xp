import DateHelper = api.util.DateHelper;

describe("api.util.DateHelper", () => {

    describe("when parseUTCDateTime", () => {

        it("given a string '2000-05-23T16:45:15' then returned Date is correct", () => {

            var date = DateHelper.makeDateFromUTCString("2000-05-23T16:45:15");
            expect(date.getUTCFullYear()).toBe(2000);
            expect(date.getUTCMonth()).toBe(4);
            expect(date.getUTCDate()).toBe(23);
            expect(date.getUTCHours()).toBe(16);
            expect(date.getUTCMinutes()).toBe(45);
            expect(date.getUTCSeconds()).toBe(15);
        });

        it("given a string '2000-05-23T00:01:00' then returned Date is correct", () => {

            var date = DateHelper.makeDateFromUTCString("2000-05-23T00:01:00");
            expect(date.getUTCFullYear()).toBe(2000);
            expect(date.getUTCMonth()).toBe(4);
            expect(date.getUTCDate()).toBe(23);
            expect(date.getUTCHours()).toBe(0);
            expect(date.getUTCMinutes()).toBe(1);
            expect(date.getUTCSeconds()).toBe(0);
        });

        it("given a string '2000-05-23T23:59:59' then returned Date is correct", () => {

            var date = DateHelper.makeDateFromUTCString("2000-05-23T23:59:59");
            expect(date.getUTCFullYear()).toBe(2000);
            expect(date.getUTCMonth()).toBe(4);
            expect(date.getUTCDate()).toBe(23);
            expect(date.getUTCHours()).toBe(23);
            expect(date.getUTCMinutes()).toBe(59);
            expect(date.getUTCSeconds()).toBe(59);
        });
    });
});