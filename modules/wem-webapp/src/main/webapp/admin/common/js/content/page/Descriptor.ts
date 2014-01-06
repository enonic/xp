module api.content.page{

    export class Descriptor{

        private name:DescriptorName;

        private displayName:string;

        private form:api.form.Form;

        constructor(builder:DescriptorBuilder) {
            this.name = builder.name;
            this.displayName = builder.displayName;
            this.form = builder.form;
        }

        getName():DescriptorName {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getForm():api.form.Form {
            return this.form;
        }
    }

    export class DescriptorBuilder {

        name:DescriptorName;

        displayName:string;

        form:api.form.Form;
    }
}