module api.util {

    export class ArrayHelper {

        static moveElement(oldIndex: number, newIndex: number, array: any[]) {

            var element = array.splice(oldIndex, 1)[0];
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
            var index = array.indexOf(value);
            if (index > -1) {
                array.splice(index, 1);
            }
        }

        static removeDuplicates(array: any[], keyFunction: (item: any) => string) {
            var seen = {};
            return array.filter(function (item) {
                var key = keyFunction(item);
                return seen.hasOwnProperty(key) ? false : (seen[key] = true);
            })
        }

    }

}
