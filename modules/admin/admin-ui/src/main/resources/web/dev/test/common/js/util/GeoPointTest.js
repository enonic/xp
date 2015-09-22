describe("api.util.GeoPointTest", function () {

    describe("when toString", function () {

        it("given a string '1.1,-2.2' then same string is returned", function () {

            var geoPoint = api.util.GeoPoint.fromString("1.1,-2.2");
            expect(geoPoint.toString()).toBe("1.1,-2.2");
        });
    });

    describe("when getLatitude", function () {

        it("given a string '1.1,-2.2' then number 1.1 is returned", function () {

            var geoPoint = api.util.GeoPoint.fromString("1.1,-2.2");
            expect(geoPoint.getLatitude()).toBe(1.1);
        });
    });

    describe("when getLongitude", function () {

        it("given a string '1.1,-2.2' then number -2.2 is returned", function () {

            var geoPoint = api.util.GeoPoint.fromString("1.1,-2.2");
            expect(geoPoint.getLongitude()).toBe(-2.2);
        });
    });

    describe("when isValidString", function () {

        it("given a string '1.1,-2.2' then true is returned", function () {

            expect(api.util.GeoPoint.isValidString("1.1,-2.2")).toBeTruthy();
        });

        it("given an empty string then false is returned", function () {

            expect(api.util.GeoPoint.isValidString("")).toBeFalsy();
        });

        it("given a string ',' then false is returned", function () {

            expect(api.util.GeoPoint.isValidString(",")).toBeFalsy();
        });

        it("given a string 'a,b' then false is returned", function () {

            expect(api.util.GeoPoint.isValidString("a,b")).toBeFalsy();
        });

        it("given a string ',-2.2' then false is returned", function () {

            expect(api.util.GeoPoint.isValidString(",-2.2")).toBeFalsy();
        });

        it("given a string '1.1,' then false is returned", function () {

            expect(api.util.GeoPoint.isValidString("1.1,")).toBeFalsy();
        });
    });
});