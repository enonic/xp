module api.macro {

    export class MacroDescriptor implements api.Equitable {

        private macroKey: MacroKey;

        private displayName: string;

        private description: string;

        private form: api.form.Form;

        private iconUrl: string;

        constructor(builder: MacroDescriptorBuilder) {
            this.macroKey = builder.macroKey;
            this.displayName = builder.displayName;
            this.description = builder.description;
            this.form = builder.form;
            this.iconUrl = builder.iconUrl;
        }

        getKey(): MacroKey {
            return this.macroKey;
        }

        getName(): string {
            return this.macroKey.getName();
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getDescription(): string {
            return this.description;
        }

        getForm(): api.form.Form {
            return this.form;
        }

        getIconUrl(): string {
            return this.iconUrl;
        }

        static create(): MacroDescriptorBuilder {
            return new MacroDescriptorBuilder();
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, MacroDescriptor)) {
                return false;
            }

            let other = <MacroDescriptor>o;

            if (this.displayName !== other.displayName) {
                return false;
            }

            if (this.description !== other.description) {
                return false;
            }

            if (this.iconUrl !== other.iconUrl) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.macroKey, other.macroKey)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.form, other.form)) {
                return false;
            }

            return true;
        }
    }

    export class MacroDescriptorBuilder {

        macroKey: MacroKey;

        displayName: string;

        description: string;

        form: api.form.Form;

        iconUrl: string;

        fromSource(source: MacroDescriptor): MacroDescriptorBuilder {
            this.macroKey = source.getKey();
            this.displayName = source.getDisplayName();
            this.description = source.getDescription();
            this.form = source.getForm();
            this.iconUrl = source.getIconUrl();
            return this;
        }

        fromJson(json: api.macro.resource.MacroJson) {
            this.macroKey = MacroKey.fromString(json.key);
            this.displayName = json.displayName;
            this.description = json.description;
            this.form = json.form !== null ? api.form.Form.fromJson(json.form) : null;
            this.iconUrl = json.iconUrl;
            return this;
        }

        setKey(key: MacroKey): MacroDescriptorBuilder {
            this.macroKey = key;
            return this;
        }

        setDisplayName(displayName: string): MacroDescriptorBuilder {
            this.displayName = displayName;
            return this;
        }

        setDescription(description: string): MacroDescriptorBuilder {
            this.description = description;
            return this;
        }

        setForm(form: api.form.Form): MacroDescriptorBuilder {
            this.form = form;
            return this;
        }

        setIconUrl(iconUrl: string): MacroDescriptorBuilder {
            this.iconUrl = iconUrl;
            return this;
        }

        build(): MacroDescriptor {
            return new MacroDescriptor(this);
        }
    }
}
