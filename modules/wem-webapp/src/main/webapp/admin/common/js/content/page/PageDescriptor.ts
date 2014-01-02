module api.content.page {

    export class PageDescriptor extends ComponentDescriptor {

    }

    export class PageDescriptorBuilder extends ComponentDescriptorBuilder {

        public fromJson(json: api.content.page.json.PageDescriptorJson): PageDescriptorBuilder {

            this.setName(new ComponentDescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setControllerResource(api.module.ModuleResourceKey.fromString(json.controller));
            this.setForm(json.configForm != null ? new api.form.Form(json.configForm) : null);
            return this;
        }

        public setName(value: ComponentDescriptorName): PageDescriptorBuilder {
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

        public setControllerResource(value: api.module.ModuleResourceKey): PageDescriptorBuilder {
            this.controllerResource = value;
            return this;
        }

        public build(): PageDescriptor {
            return new PageDescriptor(this);
        }
    }
}