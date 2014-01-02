module api.content.page.image {

    export class ImageDescriptor extends api.content.page.ComponentDescriptor {

    }

    export class ImageDescriptorBuilder extends api.content.page.ComponentDescriptorBuilder {

        public fromJson(json: api.content.page.image.json.ImageDescriptorJson): ImageDescriptorBuilder {

            this.setName(new api.content.page.ComponentDescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setControllerResource(api.module.ModuleResourceKey.fromString(json.controller));
            this.setForm(json.configForm != null ? new api.form.Form(json.configForm) : null);
            return this;
        }

        public setName(value: api.content.page.ComponentDescriptorName): ImageDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): ImageDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setForm(value: api.form.Form): ImageDescriptorBuilder {
            this.form = value;
            return this;
        }

        public setControllerResource(value: api.module.ModuleResourceKey): ImageDescriptorBuilder {
            this.controllerResource = value;
            return this;
        }

        public build(): ImageDescriptor {
            return new ImageDescriptor(this);
        }
    }
}