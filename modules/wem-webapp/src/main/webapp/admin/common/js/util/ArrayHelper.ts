module api.util {

    export class ArrayHelper {

        static moveElement(oldIndex: number, newIndex: number, array: any[]) {

            if (newIndex >= array.length) {
                var k = newIndex - array.length;
                while ((k--) + 1) {
                    array.push(undefined);
                }
            }
            array.splice(newIndex, 0, array.splice(oldIndex, 1)[0]);
        }
    }

}
