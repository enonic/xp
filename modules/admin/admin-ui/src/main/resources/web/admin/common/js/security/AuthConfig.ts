module api.security {
    export class AuthConfig implements api.Equitable {
        private applicationKey: api.application.ApplicationKey;
        private config: api.data.PropertyTree;

        constructor(builder: AuthConfigBuilder) {
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
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, AuthConfig)) {
                return false;
            }

            var other = <AuthConfig> o;

            return this.applicationKey.equals(other.applicationKey) &&
                   this.config.equals(other.config);
        }

        toJson(): AuthConfigJson {
            return {
                "applicationKey": this.applicationKey.toString(),
                "config": this.config.toJson()
            };
        }

        clone(): AuthConfig {
            return AuthConfig.create().
                setApplicationKey(this.applicationKey).
                setConfig(this.config.copy()).
                build();
        }

        static create(): AuthConfigBuilder {
            return new AuthConfigBuilder();
        }

        static fromJson(json: AuthConfigJson): AuthConfig {
            return new AuthConfigBuilder().fromJson(json).build();
        }

    }

    export class AuthConfigBuilder {
        applicationKey: api.application.ApplicationKey;
        config: api.data.PropertyTree;

        setApplicationKey(applicationKey: api.application.ApplicationKey): AuthConfigBuilder {
            this.applicationKey = applicationKey;
            return this;
        }

        setConfig(config: api.data.PropertyTree): AuthConfigBuilder {
            this.config = config;
            return this;
        }


        fromJson(json: api.security.AuthConfigJson): AuthConfigBuilder {
            this.applicationKey = api.application.ApplicationKey.fromString(json.applicationKey);
            this.config = json.config != null ? api.data.PropertyTree.fromJson(json.config) : null;
            return this;
        }

        build(): AuthConfig {
            return new AuthConfig(this);
        }
    }
}