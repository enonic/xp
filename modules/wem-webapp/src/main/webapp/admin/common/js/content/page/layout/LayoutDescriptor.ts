module api.content.page.layout {

    export class LayoutDescriptor extends api.content.page.ComponentDescriptor {

    }

    export class LayoutDescriptorBuilder extends api.content.page.ComponentDescriptorBuilder {

        public fromJson(json: api.content.page.layout.json.LayoutDescriptorJson): LayoutDescriptorBuilder {

            this.setName(new api.content.page.ComponentDescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setForm(json.configForm != null ? new api.form.Form(json.configForm) : null);
            return this;
        }

        public setName(value: api.content.page.ComponentDescriptorName): LayoutDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): LayoutDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setForm(value: api.form.Form): LayoutDescriptorBuilder {
            this.form = value;
            return this;
        }

        public build(): LayoutDescriptor {
            return new LayoutDescriptor(this);
        }
    }
}