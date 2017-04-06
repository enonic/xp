import AccessControlEntry = api.security.acl.AccessControlEntry;
import Permission = api.security.acl.Permission;

describe('api.security.acl.AccessControlEntry', () => {

    let now;

    beforeEach(() => {
        now = new Date(Date.now());
    });

    describe('equals', () => {

        let principal1: Principal = <Principal>Principal.create().setModifiedTime(now).setKey(PrincipalKey.ofAnonymous()).setDisplayName(
            'principal1').build();

        it('given an equal then true is returned', () => {

            let aclA = new AccessControlEntry(principal1);
            aclA.setAllowedPermissions([Permission.READ, Permission.CREATE]);
            aclA.setDeniedPermissions([Permission.MODIFY]);

            let aclB = new AccessControlEntry(principal1);
            aclB.setAllowedPermissions([Permission.READ, Permission.CREATE]);
            aclB.setDeniedPermissions([Permission.MODIFY]);

            expect(aclA.equals(aclB)).toBeTruthy();
        });

        it('given unequal allowed permissions then false is returned', () => {

            let aclA = new AccessControlEntry(principal1);
            aclA.setAllowedPermissions([Permission.READ, Permission.CREATE]);
            aclA.setDeniedPermissions([Permission.MODIFY]);

            let aclB = new AccessControlEntry(principal1);
            aclB.setAllowedPermissions([Permission.READ, Permission.DELETE]);
            aclB.setDeniedPermissions([Permission.MODIFY]);

            expect(aclA.equals(aclB)).toBeFalsy();
        });

    });
});
