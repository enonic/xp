module api.security {

    export class User extends Principal {

        private email: string;

        private login: string;

        private loginDisabled: boolean;

        private memberships: Principal[];

        constructor(builder: UserBuilder) {
            super(builder);
            api.util.assert(builder.key.isUser(), 'Expected PrincipalKey of type User');
            this.email = builder.email || "";
            this.login = builder.login || "";
            this.loginDisabled = builder.loginDisabled || false;
            this.memberships = builder.memberships || [];
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

        getMemberships(): Principal[] {
            return this.memberships;
        }

        setMemberships(memberships: Principal[]) {
            this.memberships = memberships;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, User)) {
                return false;
            }

            var other = <User> o;

            return super.equals(o) &&
                   this.loginDisabled === other.isDisabled() &&
                   this.email === other.getEmail() &&
                   this.login === other.getLogin() &&
//                   this.password === other.getPassword() &&
                   api.ObjectHelper.arrayEquals(this.memberships, other.getMemberships());
        }

        clone(): User {
            return this.newBuilder().build();
        }

        newBuilder(): UserBuilder {
            return new UserBuilder(this);
        }

        static create(): UserBuilder {
            return new UserBuilder();
        }

        static fromJson(json: api.security.UserJson): User {
            return new UserBuilder().fromJson(json).build();
        }

    }

    export class UserBuilder extends PrincipalBuilder {

        email: string;

        login: string;

        loginDisabled: boolean;

        memberships: Principal[];

        constructor(source?: User) {
            if (source) {
                super(source);
                this.key = source.getKey();
                this.displayName = source.getDisplayName();
                this.email = source.getEmail();
                this.login = source.getLogin();
                this.loginDisabled = source.isDisabled();
                this.modifiedTime = source.getModifiedTime();
                this.memberships = source.getMemberships().slice(0);
            } else {
                this.memberships = [];
            }
        }

        fromJson(json: api.security.UserJson): UserBuilder {
            super.fromJson(json);

            this.email = json.email;
            this.login = json.login;
            this.loginDisabled = json.loginDisabled;

            if (json.memberships) {
                this.memberships = json.memberships.map((principalJson) => Principal.fromJson(principalJson));
            }
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

        setMemberships(memberships: Principal[]): UserBuilder {
            this.memberships = memberships || [];
            return this;
        }

        build(): User {
            return new User(this);
        }
    }

}