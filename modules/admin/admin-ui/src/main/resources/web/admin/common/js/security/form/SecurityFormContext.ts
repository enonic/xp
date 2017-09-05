module api.security.form {

    export class SecurityFormContext
        extends api.form.FormContext {

        private userStore: api.security.UserStore;

        constructor(builder: SecurityFormContextBuilder) {
            super(builder);
            this.userStore = builder.userStore;
        }

        getUserStore(): api.security.UserStore {
            return this.userStore;
        }

        createInputTypeViewContext(inputTypeConfig: any, parentPropertyPath: api.data.PropertyPath,
                                   input: api.form.Input): api.form.inputtype.InputTypeViewContext {

            return <api.security.form.inputtype.SecurityInputTypeViewContext> {
                formContext: this,
                input: input,
                inputConfig: inputTypeConfig,
                parentDataPath: parentPropertyPath,
                contentPath: this.getContentPath(),
            };
        }

        private getContentPath(): api.content.ContentPath {
            return new api.content.ContentPath([this.userStore.getKey().toString()]);
        }

        static create(): SecurityFormContextBuilder {
            return new SecurityFormContextBuilder();
        }

    }

    export class SecurityFormContextBuilder
        extends api.form.FormContextBuilder {

        userStore: api.security.UserStore;

        setUserStore(value: api.security.UserStore): SecurityFormContextBuilder {
            this.userStore = value;
            return this;
        }

        public build(): SecurityFormContext {
            return new SecurityFormContext(this);
        }
    }
}
