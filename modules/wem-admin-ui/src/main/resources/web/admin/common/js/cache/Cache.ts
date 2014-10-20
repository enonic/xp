module api.cache {

    export class Cache<T,KEY> {

        private objectsByKey: {[s:string] : T;} = {};

        constructor() {

        }

        getAll(): T[] {
            var all: T[] = [];
            for (var key in this.objectsByKey) {
                if (this.objectsByKey.hasOwnProperty(key)) {
                    all.push(this.objectsByKey[key]);
                }
            }
            return all;
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
            this.objectsByKey[keyAsString] = copy;
        }

        deleteByKey(key: KEY) {
            var keyAsString = this.getKeyAsString(key);
            delete this.objectsByKey[keyAsString];
        }

        getByKey(key: KEY): T {
            var keyAsString = this.getKeyAsString(key);
            var object = this.objectsByKey[keyAsString];
            if (!object) {
                return null;
            }
            return this.copy(object);
        }
    }
}