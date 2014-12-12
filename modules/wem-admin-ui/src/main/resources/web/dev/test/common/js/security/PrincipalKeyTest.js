describe("api.security.PrincipalTest", function () {

    var PrincipalKey = api.security.PrincipalKey;

    describe("equals", function () {

        it("given an equal then true is returned", function () {

            var key1 = PrincipalKey.fromString("user:mystore:other");
            var key2 = PrincipalKey.fromString("user:mystore:other");

            expect(key1.equals(key2)).toBeTruthy();
        });

        it("given unequal id then false is returned", function () {

            var key1 = PrincipalKey.fromString("user:mystore:a");
            var key2 = PrincipalKey.fromString("user:mystore:b");

            expect(key1.equals(key2)).toBeFalsy();
        });

        it("given unequal store then false is returned", function () {

            var key1 = PrincipalKey.fromString("user:mystore:other");
            var key2 = PrincipalKey.fromString("user:otherstore:other");

            expect(key1.equals(key2)).toBeFalsy();
        });

        it("given unequal type then false is returned", function () {

            var key1 = PrincipalKey.fromString("user:mystore:other");
            var key2 = PrincipalKey.fromString("group:mystore:other");

            expect(key1.equals(key2)).toBeFalsy();
        });

    });
});