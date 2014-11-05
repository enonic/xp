module api.security {

    export class User extends Principal {

        private email: string;

        private login: string;

        private loginDisabled: boolean;

        constructor(builder: UserBuilder) {
            super(builder.key, builder.displayName, PrincipalType.USER, builder.modifiedTime);
            this.email = builder.email;
            this.login = builder.login;
            this.loginDisabled = builder.loginDisabled;
        }

        getEmail(): string {
            return this.email;
        }

        getLogin(): string {
            return this.login;
        }

        isDisabled(): boolean {
            return this.loginDisabled;
        }

        static create(): UserBuilder {
            return new UserBuilder();
        }

        static fromJson(json: api.security.UserJson): User {
            return new UserBuilder().fromJson(json).build();
        }

    }

    export class UserBuilder {

        key: PrincipalKey;

        displayName: string;

        modifiedTime: Date;

        email: string;

        login: string;

        loginDisabled: boolean;

        constructor(source?: User) {
            if (source) {
                this.key = source.getKey();
                this.displayName = source.getDisplayName();
                this.email = source.getEmail();
                this.login = source.getLogin();
                this.loginDisabled = source.isDisabled();
                this.modifiedTime = source.getModifiedTime();
            }
        }

        fromJson(json: api.security.UserJson): UserBuilder {
            this.key = PrincipalKey.fromString(json.key);
            this.displayName = json.displayName;
            this.email = json.email;
            this.login = json.login;
            this.loginDisabled = json.disabled;
            this.modifiedTime = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;
            return this;
        }

        setKey(key: PrincipalKey): UserBuilder {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): UserBuilder {
            this.displayName = displayName;
            return this;
        }

        setEmail(value: string): UserBuilder {
            this.email = value;
            return this;
        }

        setLogin(value: string): UserBuilder {
            this.login = value;
            return this;
        }

        setDisabled(value: boolean): UserBuilder {
            this.loginDisabled = value;
            return this;
        }

        build(): User {
            return new User(this);
        }
    }

}