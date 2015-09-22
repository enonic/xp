module api.security.auth {

    export class LoginResult {

        private authenticated: boolean;

        private user: api.security.User;

        private principals: api.security.PrincipalKey[];

        private applications: string[];

        private message: string;

        constructor(json: LoginResultJson) {
            this.authenticated = json.authenticated;
            if (json.user) {
                this.user = api.security.User.fromJson(json.user);
            }
            this.applications = json.applications || [];
            this.principals = json.principals ?
                              json.principals.map((principal) => api.security.PrincipalKey.fromString(principal)) : [];
            this.message = json.message;
        }

        isAuthenticated(): boolean {
            return this.authenticated;
        }

        getUser(): api.security.User {
            return this.user;
        }

        getApplications(): string[] {
            return this.applications;
        }

        getPrincipals(): api.security.PrincipalKey[] {
            return this.principals;
        }

        getMessage(): string {
            return this.message;
        }
    }
}