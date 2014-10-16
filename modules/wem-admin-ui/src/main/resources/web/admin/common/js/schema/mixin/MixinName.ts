module api.schema.mixin{

    import ModuleKey = api.module.ModuleKey;

    export class MixinName extends api.schema.ModuleBasedName {

        constructor(name:string) {
            var parts = name.split(api.schema.ModuleBasedName.SEPARATOR);
            super(ModuleKey.fromString(parts[0]), parts[1]);
        }

    }
}