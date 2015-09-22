module api.site {

    import Form = api.form.Form;
    import MixinNames = api.schema.mixin.MixinNames;

    export class SiteDescriptor implements api.Equitable {

        private form: Form;
        private metaSteps: api.schema.mixin.MixinNames;

        constructor(form: Form, mixinNames: MixinNames) {
            this.form = form;
            this.metaSteps = mixinNames;
        }

        public getForm(): Form {
            return this.form;
        }

        public getMetaSteps(): api.schema.mixin.MixinNames {
            return this.metaSteps;
        }

        static fromJson(json: api.site.json.SiteDescriptorJson): SiteDescriptor {

            return new SiteDescriptor(Form.fromJson(json.form),
                api.schema.mixin.MixinNames.create().fromStrings(json.metaSteps).build());
        }

        public equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, SiteDescriptor)) {
                return false;
            }

            var other = <SiteDescriptor>o;

            if (!api.ObjectHelper.equals(this.form, other.form)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.metaSteps, other.metaSteps)) {
                return false;
            }

            return true;
        }
    }

}