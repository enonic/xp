module api.dom {

    export class ElementManager {

        private static counters: number[] = [];

        private static elements: Element[] = [];

        public static registerElement(el: Element): string {
            var fullName;
            var id = el.getId();

            if (!id) {
                id = fullName = api.util.getFullName(el);
                var count = ElementManager.counters[fullName];
                if (count >= 0) {
                    id += '-' + (++count);
                }
            } else {
                fullName = id;
            }

            ElementManager.counters[fullName] = count || 0;
            ElementManager.elements[fullName] = el;

            return id;
        }

        public static unregisterElement(el: Element) {
            var index = ElementManager.elements.indexOf(el);
            if (index > -1) {
                ElementManager.elements.splice(index, 1);
                ElementManager.counters.splice(index, 1);
            }
        }

        public static getElementById(id: string): Element {
            return ElementManager.elements[id];
        }

    }

}