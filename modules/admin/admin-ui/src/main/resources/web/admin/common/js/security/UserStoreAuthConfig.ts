module api.security {
    export class UserStoreAuthConfig implements api.Equitable {
        private applicationKey: api.application.ApplicationKey;
        private config: api.data.PropertyTree;

        constructor(builder: UserStoreAuthConfigBuilder) {
            this.applicationKey = builder.applicationKey;
            this.config = builder.config;
        }

        getApplicationKey(): api.application.ApplicationKey {
            return this.applicationKey;
        }

        getConfig(): api.data.PropertyTree {
            return this.config;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserStoreAuthConfig)) {
                return false;
            }

            var other = <UserStoreAuthConfig> o;

            return this.applicationKey.equals(other.applicationKey) &&
                   this.config.equals(other.config)
        }

        toJson(): UserStoreAuthConfigJson {
            return {
                "applicationKey": this.applicationKey.toString(),
                "config": this.config.toJson()
            };
        }

        clone(): UserStoreAuthConfig {
            return UserStoreAuthConfig.create().
                setApplicationKey(this.applicationKey).
                setConfig(this.config.copy()).
                build();
        }

        static create(): UserStoreAuthConfigBuilder {
            return new UserStoreAuthConfigBuilder();
        }

        static fromJson(json: UserStoreAuthConfigJson): UserStoreAuthConfig {
            return new UserStoreAuthConfigBuilder().fromJson(json).build();
        }

    }

    export class UserStoreAuthConfigBuilder {
        applicationKey: api.application.ApplicationKey;
        config: api.data.PropertyTree;

        constructor() {
        }

        setApplicationKey(applicationKey: api.application.ApplicationKey): UserStoreAuthConfigBuilder {
            this.applicationKey = applicationKey;
            return this;
        }

        setConfig(config: api.data.PropertyTree): UserStoreAuthConfigBuilder {
            this.config = config;
            return this;
        }


        fromJson(json: api.security.UserStoreAuthConfigJson): UserStoreAuthConfigBuilder {
            this.applicationKey = api.application.ApplicationKey.fromString(json.applicationKey);
            this.config = json.config != null ? api.data.PropertyTree.fromJson(json.config) : null;
            return this;
        }

        build(): UserStoreAuthConfig {
            return new UserStoreAuthConfig(this);
        }
    }
}