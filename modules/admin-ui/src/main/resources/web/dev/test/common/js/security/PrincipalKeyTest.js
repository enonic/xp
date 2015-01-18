describe("api.security.PrincipalKeyTest", function () {

    var PrincipalKey = api.security.PrincipalKey;

    describe("equals", function () {

        it("given an equal then true is returned", function () {

            var key1 = PrincipalKey.fromString("user:mystore:a");
            var key2 = PrincipalKey.fromString("user:mystore:a");

            expect(key1.equals(key2)).toBeTruthy();
        });

        it("given unequal id then false is returned", function () {

            var key1 = PrincipalKey.fromString("user:mystore:a");
            var key2 = PrincipalKey.fromString("user:mystore:b");

            expect(key1.equals(key2)).toBeFalsy();
        });

        it("given unequal store then false is returned", function () {

            var key1 = PrincipalKey.fromString("user:mystore:a");
            var key2 = PrincipalKey.fromString("user:otherstore:a");

            expect(key1.equals(key2)).toBeFalsy();
        });

        it("given unequal type then false is returned", function () {

            var key1 = PrincipalKey.fromString("user:mystore:a");
            var key2 = PrincipalKey.fromString("group:mystore:a");

            expect(key1.equals(key2)).toBeFalsy();
        });

    });
});