module api {

    export class ObjectHelper {

        static iFrameSafeInstanceOf(obj: any, fn: Function): boolean {
            if (!obj) {
                return false;
            }

            if (obj instanceof fn) {
                return true;
            }
            return obj.constructor.name === (<any>fn).name;
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

            return a == b;
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

            var aString = JSON.stringify(a);
            var bString = JSON.stringify(b);
            return aString == bString;

        }
    }
}