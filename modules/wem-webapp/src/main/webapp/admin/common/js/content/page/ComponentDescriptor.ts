module api.content.page{

    export class ComponentDescriptor{

        private name:ComponentDescriptorName;

        private displayName:string;

        private form:api.form.Form;

        constructor(builder:ComponentDescriptorBuilder) {
            this.name = builder.name;
            this.displayName = builder.displayName;
            this.form = builder.form;
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
    }

    export class ComponentDescriptorBuilder {

        name:ComponentDescriptorName;

        displayName:string;

        form:api.form.Form;
    }
}