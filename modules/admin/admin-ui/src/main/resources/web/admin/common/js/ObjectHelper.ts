module api {

    /**
     * Helps with doing a IFRAME-safe instanceof and doing equals on different types of objects.
     */
    export class ObjectHelper {

        /**
         * Method to create an object of given class (useful when TS won't allow it, i.e new Event())
         * @param constructor class to use for new object
         * @param args arguments constructor arguments
         * @returns {Object}
         */
        static create(constructor, ...args) {
            var factory = constructor.bind.apply(constructor, arguments);
            return new factory();
        }

        static iFrameSafeInstanceOf(obj: any, fn: Function): boolean {
            if (!fn) {
                console.warn('Undefined fn passed to iFrameSafeInstanceOf, returning false', obj, fn);
                return false;
            }
            if (!obj) {
                return false;
            }

            if (obj instanceof fn) {
                return true;
            }

            if (ClassHelper.getClassName(obj) === ClassHelper.getFunctionName(fn)) {
                return true;
            }

            if (!(typeof obj === 'object')) {
                return false;
            }

            var prototype = Object.getPrototypeOf(obj);

            do {
                prototype = Object.getPrototypeOf(prototype);
                if (!prototype) {
                    return false;
                }
            }
            while (ClassHelper.getClassName(prototype) !== ClassHelper.getFunctionName(fn));

            return true;
        }

        static equals(a: Equitable, b: Equitable) {

            if (!a && !b) {
                return true;
            }
            else if (!a && b) {
                return false;
            }
            else if (a && !b) {
                return false;
            }

            return a.equals(b);
        }

        static arrayEquals(arrayA: Equitable[], arrayB: Equitable[]) {

            if (!arrayA && !arrayB) {
                return true;
            }
            else if (!arrayA && arrayB) {
                return false;
            }
            else if (arrayA && !arrayB) {
                return false;
            }

            if (arrayA.length != arrayB.length) {
                return false;
            }

            for (var i = 0; i < arrayA.length; i++) {
                if (!ObjectHelper.equals(arrayA[i], arrayB[i])) {
                    return false;
                }
            }

            return true;
        }

        static anyArrayEquals(arrayA: any[], arrayB: any[]) {

            if (!arrayA && !arrayB) {
                return true;
            }
            else if (!arrayA && arrayB) {
                return false;
            }
            else if (arrayA && !arrayB) {
                return false;
            }

            if (arrayA.length != arrayB.length) {
                return false;
            }

            for (var i = 0; i < arrayA.length; i++) {
                if (!ObjectHelper.objectEquals(arrayA[i], arrayB[i])) {
                    return false;
                }
            }

            return true;
        }

        static mapEquals(mapA: {[s:string] : Equitable;}, mapB: {[s:string] : Equitable;}) {

            if (!mapA && !mapB) {
                return true;
            }
            else if (!mapA && mapB) {
                return false;
            }
            else if (mapA && !mapB) {
                return false;
            }

            // Gather keys for both maps
            var keysA: string[] = [];
            for (var keyA  in mapA) {
                if (mapA.hasOwnProperty(keyA)) {
                    keysA.push(keyA);
                }
            }
            var keysB: string[] = [];
            for (var keyB  in mapB) {
                if (mapB.hasOwnProperty(keyB)) {
                    keysB.push(keyB);
                }
            }

            if (!ObjectHelper.stringArrayEquals(keysA, keysB)) {
                return false;
            }

            for (var keyA  in keysA) {
                var valueA: Equitable = mapA[keysA[keyA]];
                var valueB: Equitable = mapB[keysA[keyA]];

                if (!ObjectHelper.equals(valueA, valueB)) {
                    return false;
                }
            }

            return true;
        }

        static stringEquals(a: string, b: string) {

            if (!a && !b) {
                return true;
            }
            else if (!a && b) {
                return false;
            }
            else if (a && !b) {
                return false;
            }

            return a.toString() === b.toString();
        }

        static stringArrayEquals(arrayA: string[], arrayB: string[]) {

            if (!arrayA && !arrayB) {
                return true;
            }
            else if (!arrayA && arrayB) {
                return false;
            }
            else if (arrayA && !arrayB) {
                return false;
            }

            if (arrayA.length != arrayB.length) {
                return false;
            }

            for (var i = 0; i < arrayA.length; i++) {
                if (!ObjectHelper.stringEquals(arrayA[i], arrayB[i])) {
                    return false;
                }
            }

            return true;
        }

        static booleanEquals(a: boolean, b: boolean) {

            if (!a && !b) {
                return true;
            }
            else if (!a && b) {
                return false;
            }
            else if (a && !b) {
                return false;
            }

            return a == b;
        }

        static numberEquals(a: number, b: number) {

            if (!a && !b) {
                return true;
            }
            else if (!a && b) {
                return false;
            }
            else if (a && !b) {
                return false;
            }

            return a == b;
        }

        static dateEquals(a: Date, b: Date) {

            if (!a && !b) {
                return true;
            }
            else if (!a && b) {
                return false;
            }
            else if (a && !b) {
                return false;
            }

            return a.toISOString() == b.toISOString();
        }

        static anyEquals(a: any, b: any) {

            if (!a && !b) {
                return true;
            }
            else if (!a && b) {
                return false;
            }
            else if (a && !b) {
                return false;
            }

            var aString = JSON.stringify(a);
            var bString = JSON.stringify(b);
            return aString == bString;
        }

        static objectEquals(a: Object, b: Object) {

            if (!a && !b) {
                return true;
            }
            else if (!a && b) {
                return false;
            }
            else if (a && !b) {
                return false;
            }

            /*
             To avoid exception, when converting circular structure to JSON in Chrome the replacer
             function must be used to replace references to the same object with `undefined`.
             */
            var aString = JSON.stringify(a, (key, value) => {
                return (!!key && a == value) ? undefined : value;
            });
            var bString = JSON.stringify(b, (key, value) => {
                return (!!key && b == value) ? undefined : value;
            });
            return aString == bString;

        }

        static objectPropertyIterator(object: any, callback: {(name: string, property: any, index?: number): void;}) {

            var index = 0;
            for (var name  in object) {
                if (object.hasOwnProperty(name)) {
                    var property = object[name];
                    callback(name, property, index++);
                }
            }
        }
    }
}
