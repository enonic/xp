module api.content.page.image {

    export class ImageDescriptor extends api.content.page.Descriptor {

    }

    export class ImageDescriptorBuilder extends api.content.page.DescriptorBuilder {

        public fromJson(json: api.content.page.image.json.ImageDescriptorJson): ImageDescriptorBuilder {

            this.setName(new api.content.page.DescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setConfig(json.config != null ? new api.form.Form(json.config) : null);
            return this;
        }

        public setName(value: api.content.page.DescriptorName): ImageDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): ImageDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setConfig(value: api.form.Form): ImageDescriptorBuilder {
            this.config = value;
            return this;
        }

        public build(): ImageDescriptor {
            return new ImageDescriptor(this);
        }
    }
}