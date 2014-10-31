module api.security.auth {

    export class LoginCredentials {

        private user: string;

        private userStore: api.security.UserStoreKey;

        private password: string;

        private rememberMe: boolean;

        constructor() {
            this.rememberMe = false;
        }

        getUser(): string {
            return this.user;
        }

        getPassword(): string {
            return this.password;
        }

        getUserStore(): api.security.UserStoreKey {
            return this.userStore;
        }

        isRememberMe(): boolean {
            return this.rememberMe;
        }

        setUser(user: string): LoginCredentials {
            this.user = user;
            return this;
        }

        setPassword(pwd: string): LoginCredentials {
            this.password = pwd;
            return this;
        }

        setUserStore(userStore: api.security.UserStoreKey): LoginCredentials {
            this.userStore = userStore;
            return this;
        }

        setRememberMe(value: boolean): LoginCredentials {
            this.rememberMe = value;
            return this;
        }

    }

}
