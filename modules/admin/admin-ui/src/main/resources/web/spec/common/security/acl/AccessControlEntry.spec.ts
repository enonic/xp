import AccessControlEntry = api.security.acl.AccessControlEntry;
import Permission = api.security.acl.Permission;

describe("api.security.acl.AccessControlEntry", () => {

    var now;

    beforeEach(() => {
        now = new Date(Date.now());
    });

    describe("equals", () => {

        var principal1 = Principal.create().setKey(PrincipalKey.ofAnonymous()).setDisplayName("principal1").setModifiedTime(now).build();

        it("given an equal then true is returned", () => {

            var aclA = new AccessControlEntry(principal1);
            aclA.setAllowedPermissions([Permission.READ, Permission.CREATE]);
            aclA.setDeniedPermissions([Permission.MODIFY]);

            var aclB = new AccessControlEntry(principal1);
            aclB.setAllowedPermissions([Permission.READ, Permission.CREATE]);
            aclB.setDeniedPermissions([Permission.MODIFY]);

            expect(aclA.equals(aclB)).toBeTruthy();
        });

        it("given unequal allowed permissions then false is returned", () => {

            var aclA = new AccessControlEntry(principal1);
            aclA.setAllowedPermissions([Permission.READ, Permission.CREATE]);
            aclA.setDeniedPermissions([Permission.MODIFY]);

            var aclB = new AccessControlEntry(principal1);
            aclB.setAllowedPermissions([Permission.READ, Permission.DELETE]);
            aclB.setDeniedPermissions([Permission.MODIFY]);

            expect(aclA.equals(aclB)).toBeFalsy();
        });

    });
});