import Principal = api.security.Principal;

describe('api.security.Principal', () => {

    let now;
    let later;

    beforeAll(() => {
        now = new Date(Date.now());
        later = new Date(Date.now() + 100000);
    });

    describe('equals', () => {

        it('given an equal then true is returned', () => {

            let principal1 = Principal.create().setKey(PrincipalKey.ofAnonymous()).setDisplayName('Anon').setModifiedTime(now).build();
            let principal2 = Principal.create().setKey(PrincipalKey.ofAnonymous()).setDisplayName('Anon').setModifiedTime(now).build();

            expect(principal1.equals(principal2)).toBeTruthy();
        });

        it('given unequal displayName then false is returned', () => {

            let principal1 = Principal.create().setKey(PrincipalKey.ofAnonymous()).setDisplayName('Anon').setModifiedTime(now).build();
            let principal2 = Principal.create().setKey(PrincipalKey.ofAnonymous()).setDisplayName('Other').setModifiedTime(now).build();

            expect(principal1.equals(principal2)).toBeFalsy();
        });

        it('given unequal type then false is returned', () => {

            let principal1 = Principal.create().setKey(PrincipalKey.fromString('user:mystore:other')).setDisplayName(
                'Anon').setModifiedTime(now).build();
            let principal2 = Principal.create().setKey(PrincipalKey.fromString('user:mystore:other')).setDisplayName(
                'Anon').setModifiedTime(later).build();

            expect(principal1.equals(principal2)).toBeFalsy();
        });

    });
});
