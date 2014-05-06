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
    }

}
