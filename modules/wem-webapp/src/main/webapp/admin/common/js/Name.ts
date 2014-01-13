module api {

    export class Name {

        private value:string;

        constructor(name:string) {
            var nameRegExp = new RegExp("^[_a-z0-9]([a-z0-9_\\-\\.])*$");
            api.util.assert(!name, "Name cannot be null");

            api.util.assert(!name.trim(),"Name cannot be empty");

            api.util.assert(nameRegExp.test(name),
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