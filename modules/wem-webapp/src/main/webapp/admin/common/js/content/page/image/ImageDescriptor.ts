module api.content.page.image {

    export class ImageDescriptor extends api.content.page.Descriptor {

    }

    export class ImageDescriptorBuilder extends api.content.page.DescriptorBuilder {

        public fromJson(json: api.content.page.image.json.ImageDescriptorJson): ImageDescriptorBuilder {

            this.setName(new api.content.page.DescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setForm(json.configForm != null ? new api.form.Form(json.configForm) : null);
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

        public setForm(value: api.form.Form): ImageDescriptorBuilder {
            this.form = value;
            return this;
        }

        public build(): ImageDescriptor {
            return new ImageDescriptor(this);
        }
    }
}