module api.ui.security.acl {

    import PrincipalKey = api.security.PrincipalKey;
    import Principal = api.security.Principal;

    export class EffectivePermissionMember {

        private userKey: PrincipalKey;

        private displayName: string;

        constructor(userKey: PrincipalKey, displayName: string) {
            this.userKey = userKey;
            this.displayName = displayName;
        }

        getUserKey(): PrincipalKey {
            return this.userKey;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        toPrincipal(): Principal {
            return <Principal>Principal.create().setKey(this.userKey).setDisplayName(this.displayName).build();
        }

        static fromJson(json: api.content.json.EffectivePermissionMemberJson) {
            return new EffectivePermissionMember(PrincipalKey.fromString(json.key), json.displayName);
        }

    }

}
