module api.schema.mixin{

    import ApplicationKey = api.module.ApplicationKey;

    export class MixinName extends api.module.ModuleBasedName {

        constructor(name:string) {
            api.util.assertNotNull(name, "Mixin name can't be null");
            var parts = name.split(api.module.ModuleBasedName.SEPARATOR);
            super(ApplicationKey.fromString(parts[0]), parts[1]);
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, MixinName)) {
                return false;
            }

            return super.equals(o);
        }
    }
}