module api_content_page{

    export class ComponentDescriptor{

        private name:ComponentDescriptorName;

        private displayName:string;

        private form:api_form.Form;

        private controllerResource:api_module.ModuleResourceKey;

        constructor(builder:ComponentDescriptorBuilder) {
            this.name = builder.name;
            this.displayName = builder.displayName;
            this.form = builder.form;
            this.controllerResource = builder.controllerResource;
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

    }
}