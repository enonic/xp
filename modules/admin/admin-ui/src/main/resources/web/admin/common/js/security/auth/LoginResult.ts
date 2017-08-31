module api.security.auth {

    export class LoginResult {

        private authenticated: boolean;

        private user: api.security.User;

        private principals: api.security.PrincipalKey[];

        private message: string;

        constructor(json: LoginResultJson) {
            this.authenticated = json.authenticated;
            if (json.user) {
                this.user = api.security.User.fromJson(json.user);
            }
            this.principals = json.principals ?
                              json.principals.map((principal) => api.security.PrincipalKey.fromString(principal)) : [];
            this.message = json.message;
        }

        isAuthenticated(): boolean {
            return this.authenticated;
        }

        isContentAdmin(): boolean {
            return this.principals.some(principalKey => RoleKeys.isContentAdmin(principalKey));
        }

        isContentExpert(): boolean {
            return this.principals.some(principalKey => RoleKeys.isContentExpert(principalKey));
        }

        getUser(): api.security.User {
            return this.user;
        }

        getPrincipals(): api.security.PrincipalKey[] {
            return this.principals;
        }

        getMessage(): string {
            return this.message;
        }
    }
}
