module api.util {

    export class ArrayHelper {

        static moveElement(oldIndex: number, newIndex: number, array: any[]) {

            let element = array.splice(oldIndex, 1)[0];
            if (newIndex >= array.length) {
                array[newIndex] = element;
            } else {
                array.splice(newIndex, 0, element);
            }

        }

        static addUnique(value: any, array: any[]) {
            if (array.indexOf(value) === -1) {
                array.push(value);
            }
        }

        static removeValue(value: any, array: any[]) {
            let index = array.indexOf(value);
            if (index > -1) {
                array.splice(index, 1);
            }
        }

        static removeDuplicates<T>(array: T[], keyFunction: (item: T)=> string): T[] {
            let seen = {};
            return array.filter((item) => {
                let key = keyFunction(item);
                if (!seen.hasOwnProperty(key)) {
                    seen[key] = true;
                    return true;
                }
                return false;
            });
        }

        // Non-symmetric difference of A and B
        // Will return all values from A, that is absent in B
        static difference<T>(left: T[], right: T[], equals: (valueLeft: T, valueRight: T) => boolean): T[] {
            return left.filter((value) => {
                for (let i = 0; i < right.length; i++) {
                    if (equals(value, right[i])) {
                        return false;
                    }
                }
                return true;
            });
        }

        static intersection<T>(left: T[], right: T[], equals: (valueLeft: T, valueRight: T) => boolean): T[] {
            return left.filter((value) => {
                for (let i = 0; i < right.length; i++) {
                    if (equals(value, right[i])) {
                        return true;
                    }
                }
                return false;
            });
        }

        static findElementByFieldValue<T>(array: Array<T>, field: string, value: any): T {
            let result: T = null;

            array.every((element: T) => {
                if (element[field] === value) {
                    result = element;
                    return false;
                }
                return true;
            });

            return result;
        }

        static contains(array: Equitable[], el: Equitable): boolean {
            if (array && array.length > 0) {
                return array.some((curEl) => {
                    return curEl.equals(el);
                });
            }
            return false;
        }

        static filter(array:Equitable[], el: Equitable):Equitable[] {
            return array.filter((curEl) => {
                return !curEl.equals(el);
            });
        }
    }

}
