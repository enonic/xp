import GeoPoint = api.util.GeoPoint;

describe("GeoPoint", () => {

    describe("when toString", () => {

        it("given a string '1.1,-2.2' then same string is returned", () => {

            var geoPoint = GeoPoint.fromString("1.1,-2.2");
            expect(geoPoint.toString()).toBe("1.1,-2.2");
        });
    });

    describe("when getLatitude", () => {

        it("given a string '1.1,-2.2' then number 1.1 is returned", () => {

            var geoPoint = GeoPoint.fromString("1.1,-2.2");
            expect(geoPoint.getLatitude()).toBe(1.1);
        });
    });

    describe("when getLongitude", () => {

        it("given a string '1.1,-2.2' then number -2.2 is returned", () => {

            var geoPoint = GeoPoint.fromString("1.1,-2.2");
            expect(geoPoint.getLongitude()).toBe(-2.2);
        });
    });

    describe("when isValidString", () => {

        it("given a string '1.1,-2.2' then true is returned", () => {

            expect(GeoPoint.isValidString("1.1,-2.2")).toBeTruthy();
        });

        it("given an empty string then false is returned", () => {

            expect(GeoPoint.isValidString("")).toBeFalsy();
        });

        it("given a string ',' then false is returned", () => {

            expect(GeoPoint.isValidString(",")).toBeFalsy();
        });

        it("given a string 'a,b' then false is returned", () => {

            expect(GeoPoint.isValidString("a,b")).toBeFalsy();
        });

        it("given a string ',-2.2' then false is returned", () => {

            expect(GeoPoint.isValidString(",-2.2")).toBeFalsy();
        });

        it("given a string '1.1,' then false is returned", () => {

            expect(GeoPoint.isValidString("1.1,")).toBeFalsy();
        });
    });
});