module api {

    export class Name {

        private static NAME_REG_EXP = new RegExp("^[_a-z0-9]([a-z0-9_\\-\\.])*$");

        private value:string;

        constructor(name:string) {

            api.util.assertNotNull(name, "Name cannot be null");

            api.util.assert(!api.util.isStringEmpty(name),"Name cannot be empty");

            api.util.assert(Name.NAME_REG_EXP.test(name),
                "A name can only start with lower case latin letters or digit, and further consist of the same, digits or the following special chars: _-.: " + name);

            this.value = name;
        }

        getValue():string {
            return this.value;
        }

        toString():string {
            return this.value;
        }
    }
}