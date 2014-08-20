module api.module {

    export class Module extends api.module.ModuleSummary {

        private config: api.form.Form;

        private moduleDependencies: api.module.ModuleKey[] = [];

        private contentTypeDependencies: api.schema.content.ContentTypeName[] = [];

        private minSystemVersion: string;

        private maxSystemVersion: string;

        constructor(builder: ModuleBuilder) {
            super(builder);

            this.config = builder.config;
            this.moduleDependencies = builder.moduleDependencies;
            this.contentTypeDependencies = builder.contentTypeDependencies;
            this.minSystemVersion = builder.minSystemVersion;
            this.maxSystemVersion = builder.maxSystemVersion;
        }

        getForm(): api.form.Form {
            return this.config;
        }

        getMinSystemVersion(): string {
            return this.minSystemVersion;
        }

        getMaxSystemVersion(): string {
            return this.maxSystemVersion;
        }

        getModuleDependencies(): api.module.ModuleKey[] {
            return this.moduleDependencies;
        }

        getContentTypeDependencies(): api.schema.content.ContentTypeName[] {
            return this.contentTypeDependencies;
        }

        static fromJson(json: api.module.json.ModuleJson): Module {
            return new ModuleBuilder().fromJson(json).build();
        }

        static fromJsonArray(jsonArray: api.module.json.ModuleJson[]): Module[] {
            var array: Module[] = [];
            jsonArray.forEach((json: api.module.json.ModuleJson) => {
                array.push(Module.fromJson(json));
            });
            return array;
        }
    }

    export class ModuleBuilder extends ModuleSummaryBuilder {

        config: api.form.Form;

        moduleDependencies: api.module.ModuleKey[];

        contentTypeDependencies: api.schema.content.ContentTypeName[];

        minSystemVersion: string;

        maxSystemVersion: string;

        constructor(source?: Module) {
            this.moduleDependencies = [];
            this.contentTypeDependencies = [];
            if (source) {
                super(source);
                this.config = source.getForm();
                this.moduleDependencies = source.getModuleDependencies();
                this.contentTypeDependencies = source.getContentTypeDependencies();
                this.minSystemVersion = source.getMinSystemVersion();
                this.maxSystemVersion = source.getMaxSystemVersion();
            }
        }

        fromJson(json: api.module.json.ModuleJson): ModuleBuilder {
            super.fromJson(json);

            this.config = json.config != null ? new api.form.Form(json.config) : null;
            this.minSystemVersion = json.minSystemVersion;
            this.maxSystemVersion = json.maxSystemVersion;

            if (json.moduleDependencies != null) {
                json.moduleDependencies.forEach((dependency: string) => {
                    this.moduleDependencies.push(api.module.ModuleKey.fromString(dependency));
                });
            }

            if (json.contentTypeDependencies != null) {
                json.contentTypeDependencies.forEach((dependency: string) => {
                    this.contentTypeDependencies.push(new api.schema.content.ContentTypeName(dependency));
                });
            }

            return this;
        }

        build(): Module {
            return new Module(this);
        }
    }
}