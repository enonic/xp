module api.content.page {

    export class PageDescriptor extends Descriptor {

    }

    export class PageDescriptorBuilder extends DescriptorBuilder {

        public fromJson(json: api.content.page.json.PageDescriptorJson): PageDescriptorBuilder {

            this.setName(new DescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setForm(json.configForm != null ? new api.form.Form(json.configForm) : null);
            return this;
        }

        public setName(value: DescriptorName): PageDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): PageDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setForm(value: api.form.Form): PageDescriptorBuilder {
            this.form = value;
            return this;
        }

        public build(): PageDescriptor {
            return new PageDescriptor(this);
        }
    }
}