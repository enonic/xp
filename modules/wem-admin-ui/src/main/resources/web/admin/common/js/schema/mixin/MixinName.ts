module api.schema.mixin{

    import ModuleKey = api.module.ModuleKey;

    export class MixinName extends api.module.ModuleBasedName {

        constructor(name:string) {
            api.util.assertNotNull(name, "Mixin name can't be null");
            var parts = name.split(api.module.ModuleBasedName.SEPARATOR);
            super(ModuleKey.fromString(parts[0]), parts[1]);
        }

    }
}