describe("api.security.PrincipalTest", function () {

    var Principal = api.security.Principal;
    var PrincipalKey = api.security.PrincipalKey;

    var now = new Date(Date.now());

    describe("equals", function () {

        it("given an equal then true is returned", function () {

            var principal1 = new Principal(PrincipalKey.ofAnonymous(), "anon", now);
            var principal2 = new Principal(PrincipalKey.ofAnonymous(), "anon", now);

            expect(principal1.equals(principal2)).toBeTruthy();
        });

        it("given unequal displayName then false is returned", function () {

            var principal1 = new Principal(PrincipalKey.ofAnonymous(), "anon", now);
            var principal2 = new Principal(PrincipalKey.ofAnonymous(), "other", now);

            expect(principal1.equals(principal2)).toBeFalsy();
        });

        it("given unequal type then false is returned", function () {

            var principal1 = new Principal(PrincipalKey.fromString("user:mystore:other"), "Other", now);
            var principal2 = new Principal(PrincipalKey.fromString("group:mystore:other"), "Other", now);

            expect(principal1.equals(principal2)).toBeFalsy();
        });

    });
});