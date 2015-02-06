module api.security.auth {

    export class LoginResult {

        private authenticated: boolean;

        private user: api.security.User;

        private applications: string[];

        constructor(json: LoginResultJson) {
            this.authenticated = json.authenticated;
            if (json.user) {
                this.user = api.security.User.fromJson(json.user);
            }
            this.applications = json.applications || [];
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
    }
}