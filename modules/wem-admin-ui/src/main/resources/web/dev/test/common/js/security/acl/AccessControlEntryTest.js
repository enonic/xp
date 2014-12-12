describe("api.security.acl.AccessControlEntryTest", function () {

    var AccessControlEntry = api.security.acl.AccessControlEntry;
    var Permission = api.security.acl.Permission;
    var Principal = api.security.Principal;
    var PrincipalKey = api.security.PrincipalKey;

    var now = new Date(Date.now());

    describe("equals", function () {

        var principal1 = new Principal(PrincipalKey.ofAnonymous(), "principal1", now);

        it("given an equal then true is returned", function () {

            var aclA = new AccessControlEntry(principal1);
            aclA.setAllowedPermissions([Permission.READ, Permission.CREATE]);
            aclA.setDeniedPermissions(Permission.MODIFY);

            var aclB = new AccessControlEntry(principal1);
            aclB.setAllowedPermissions([Permission.READ, Permission.CREATE]);
            aclB.setDeniedPermissions(Permission.MODIFY);

            expect(aclA.equals(aclB)).toBeTruthy();
        });

        it("given unequal allowed permissions then false is returned", function () {

            var aclA = new AccessControlEntry(principal1);
            aclA.setAllowedPermissions([Permission.READ, Permission.CREATE]);
            aclA.setDeniedPermissions(Permission.MODIFY);

            var aclB = new AccessControlEntry(principal1);
            aclB.setAllowedPermissions([Permission.READ, Permission.DELETE]);
            aclB.setDeniedPermissions(Permission.MODIFY);

            expect(aclA.equals(aclB)).toBeFalsy();
        });

    });
});