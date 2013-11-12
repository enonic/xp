module api_content_page{

    export class ComponentDescriptor{

        private name:ComponentDescriptorName;

        private displayName:string;

        private form:api_form.Form;

        private controllerResource:api_module.ModuleResourceKey;

        constructor(builder:ComponentDescriptorBuilder) {
            this.name = builder.name;
        }

        getName():ComponentDescriptorName {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getForm():api_form.Form {
            return this.form;
        }

        getControllerResource():api_module.ModuleResourceKey {
            return this.controllerResource;
        }
    }

    export class ComponentDescriptorBuilder {

        name:ComponentDescriptorName;

        displayName:string;

        form:api_form.Form;

        controllerResource:api_module.ModuleResourceKey;

        public setName(value:ComponentDescriptorName):ComponentDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value:string):ComponentDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setForm(value:api_form.Form):ComponentDescriptorBuilder {
            this.form = value;
            return this;
        }

        public setControllerResource(value:api_module.ModuleResourceKey):ComponentDescriptorBuilder {
            this.controllerResource = value;
            return this;
        }
    }
}