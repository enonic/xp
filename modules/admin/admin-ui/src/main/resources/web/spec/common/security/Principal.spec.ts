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

            let principal1: Principal = <Principal>Principal.create().setModifiedTime(now).setKey(
                PrincipalKey.ofAnonymous()).setDisplayName('Anon').build();
            let principal2: Principal = <Principal>Principal.create().setModifiedTime(now).setKey(
                PrincipalKey.ofAnonymous()).setDisplayName('Anon').build();

            expect(principal1.equals(principal2)).toBeTruthy();
        });

        it('given unequal displayName then false is returned', () => {

            let principal1: Principal = <Principal>Principal.create().setModifiedTime(now).setKey(
                PrincipalKey.ofAnonymous()).setDisplayName('Anon').build();
            let principal2: Principal = <Principal>Principal.create().setModifiedTime(now).setKey(
                PrincipalKey.ofAnonymous()).setDisplayName('Other').build();

            expect(principal1.equals(principal2)).toBeFalsy();
        });

        it('given unequal type then false is returned', () => {

            let principal1: Principal = <Principal>Principal.create().setModifiedTime(now).setKey(
                PrincipalKey.fromString('user:mystore:other')).setDisplayName(
                'Anon').build();
            let principal2: Principal = <Principal>Principal.create().setModifiedTime(later).setKey(
                PrincipalKey.fromString('user:mystore:other')).setDisplayName(
                'Anon').build();

            expect(principal1.equals(principal2)).toBeFalsy();
        });

    });
});
