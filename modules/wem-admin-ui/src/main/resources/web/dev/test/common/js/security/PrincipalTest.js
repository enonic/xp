describe("api.security.PrincipalTest", function () {

    var Principal = api.security.Principal;
    var PrincipalKey = api.security.PrincipalKey;

    var now = new Date(Date.now());
    var later = new Date(Date.now() + 100000);

    describe("equals", function () {

        it("given an equal then true is returned", function () {

            var principal1 = new Principal(PrincipalKey.ofAnonymous(), "Anon", now);
            var principal2 = new Principal(PrincipalKey.ofAnonymous(), "Anon", now);

            expect(principal1.equals(principal2)).toBeTruthy();
        });

        it("given unequal displayName then false is returned", function () {

            var principal1 = new Principal(PrincipalKey.ofAnonymous(), "Anon", now);
            var principal2 = new Principal(PrincipalKey.ofAnonymous(), "Other", now);

            expect(principal1.equals(principal2)).toBeFalsy();
        });

        it("given unequal type then false is returned", function () {

            var principal1 = new Principal(PrincipalKey.fromString("user:mystore:other"), "Other", now);
            var principal2 = new Principal(PrincipalKey.fromString("user:mystore:other"), "Other", later);

            expect(principal1.equals(principal2)).toBeFalsy();
        });

    });
});