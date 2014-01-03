module api.content.page{

    export class ComponentDescriptor{

        private name:ComponentDescriptorName;

        private displayName:string;

        private form:api.form.Form;

        private controllerResource:api.module.ModuleResourceKey;

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

        getForm():api.form.Form {
            return this.form;
        }

        getControllerResource():api.module.ModuleResourceKey {
            return this.controllerResource;
        }
    }

    export class ComponentDescriptorBuilder {

        name:ComponentDescriptorName;

        displayName:string;

        form:api.form.Form;

        controllerResource:api.module.ModuleResourceKey;

    }
}