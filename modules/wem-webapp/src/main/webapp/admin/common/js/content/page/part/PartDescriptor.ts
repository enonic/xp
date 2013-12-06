module api_content_page_image {

    export class PartDescriptor extends api_content_page.ComponentDescriptor {

    }

    export class PartDescriptorBuilder extends api_content_page.ComponentDescriptorBuilder {

        public fromJson(json: api_content_page_part_json.PartDescriptorJson): PartDescriptorBuilder {

            this.setName(new api_content_page.ComponentDescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setControllerResource(api_module.ModuleResourceKey.fromString(json.controller));
            this.setForm(json.configForm != null ? new api_form.Form(json.configForm) : null);
            return this;
        }

        public setName(value:api_content_page.ComponentDescriptorName):PartDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value:string):PartDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setForm(value:api_form.Form):PartDescriptorBuilder {
            this.form = value;
            return this;
        }

        public setControllerResource(value:api_module.ModuleResourceKey):PartDescriptorBuilder {
            this.controllerResource = value;
            return this;
        }

        public build(): PartDescriptor {
            return new PartDescriptor(this);
        }
    }
}