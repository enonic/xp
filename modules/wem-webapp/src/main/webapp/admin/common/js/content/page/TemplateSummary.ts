module api_content_page{

    export class TemplateSummary<KEY extends TemplateKey,NAME extends TemplateName> {

        private key:KEY;

        private name:NAME;

        private displayName:string;

        private descriptorModuleResourceKey:api_module.ModuleResourceKey;

        constructor(builder:TemplateSummaryBuilder<KEY,NAME>) {
            this.name = builder.name;
            this.displayName = builder.displayName;
            this.descriptorModuleResourceKey = builder.descriptorModuleResourceKey;
        }

        getKey():KEY {
            return this.key;
        }

        getName():NAME {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getDescriptorModuleResourceKey():api_module.ModuleResourceKey {
            return this.descriptorModuleResourceKey;
        }
    }

    export class TemplateSummaryBuilder<KEY extends TemplateKey,NAME extends TemplateName> {

        key:KEY;

        name:NAME;

        displayName:string;

        descriptorModuleResourceKey:api_module.ModuleResourceKey;

        public setKey(value:KEY):TemplateSummaryBuilder<KEY,NAME> {
            this.key = value;
            return this;
        }

        public setName(value:NAME):TemplateSummaryBuilder<KEY,NAME> {
            this.name = value;
            return this;
        }

        public setDisplayName(value:string):TemplateSummaryBuilder<KEY,NAME> {
            this.displayName = value;
            return this;
        }

        public setDescriptorModuleResourceKey(value:api_module.ModuleResourceKey):TemplateSummaryBuilder<KEY,NAME> {
            this.descriptorModuleResourceKey = value;
            return this;
        }

    }
}