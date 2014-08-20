module api.cache {

    export class Cache<T,KEY> {

        private objectsTypesByKey: {[s:string] : T;} = {};

        constructor() {

        }

        copy(object: T): T {
            throw new Error("Must be implemented by inheritor");
        }

        getKeyFromObject(object: T): KEY {
            throw new Error("Must be implemented by inheritor");
        }

        getKeyAsString(object: KEY): string {
            throw new Error("Must be implemented by inheritor");
        }

        put(object: T) {
            var copy = this.copy(object);
            var keyAsString = this.getKeyAsString(this.getKeyFromObject(object));
            this.objectsTypesByKey[keyAsString] = copy;
        }

        deleteByKey(key: KEY) {
            var keyAsString = this.getKeyAsString(key);
            delete this.objectsTypesByKey[keyAsString];
        }

        getByKey(key: KEY): T {
            var keyAsString = this.getKeyAsString(key);
            var object = this.objectsTypesByKey[keyAsString];
            if (!object) {
                return null;
            }
            return this.copy(object);
        }
    }
}