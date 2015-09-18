module api.system {

    export class StatusResult {

        private installation: string;

        private version: string;

        private authenticated: boolean;

        private principals: api.security.PrincipalKey[];

        constructor(json: StatusJson) {
            this.version = json.version;
            this.installation = json.installation;
            if (json.context) {
                this.authenticated = json.context.authenticated;
                this.principals = json.context.principals ?
                                  json.context.principals.map((principal) => api.security.PrincipalKey.fromString(principal)) : [];
            } else {
                this.authenticated = false;
                this.principals = []
            }
        }

        isAuthenticated(): boolean {
            return this.authenticated;
        }


        getInstallation(): string {
            return this.installation;
        }

        getVersion(): string {
            return this.version;
        }

        getPrincipals(): api.security.PrincipalKey[] {
            return this.principals;
        }
    }
}