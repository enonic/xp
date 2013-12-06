module api_content_page_image {

    export class ImageDescriptor extends api_content_page.ComponentDescriptor {

    }

    export class ImageDescriptorBuilder extends api_content_page.ComponentDescriptorBuilder {

        public fromJson(json: api_content_page_image_json.ImageDescriptorJson): ImageDescriptorBuilder {

            this.setName(new api_content_page.ComponentDescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setControllerResource(api_module.ModuleResourceKey.fromString(json.controller));
            this.setForm(json.configForm != null ? new api_form.Form(json.configForm) : null);
            return this;
        }

        public setName(value: api_content_page.ComponentDescriptorName): ImageDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): ImageDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setForm(value: api_form.Form): ImageDescriptorBuilder {
            this.form = value;
            return this;
        }

        public setControllerResource(value: api_module.ModuleResourceKey): ImageDescriptorBuilder {
            this.controllerResource = value;
            return this;
        }

        public build(): ImageDescriptor {
            return new ImageDescriptor(this);
        }
    }
}