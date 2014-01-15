module api.dom {

    export class ElementRegistry {

        private static counters: number[] = [];

        private static elements: Element[] = [];

        public static registerElement(el: Element): string {
            var fullName;
            var id = el.getId();

            if (!id) {
                id = fullName = api.util.getFullName(el);
            } else {
                fullName = id;
            }
            var count = ElementRegistry.counters[fullName];
            if (count >= 0) {
                id += '-' + (++count);
            }

            ElementRegistry.counters[fullName] = count || 0;
            ElementRegistry.elements[fullName] = el;

            return id;
        }

        public static unregisterElement(el: Element) {
            var index = ElementRegistry.elements.indexOf(el);
            if (index > -1) {
                ElementRegistry.elements.splice(index, 1);
                ElementRegistry.counters.splice(index, 1);
            }
        }

        public static getElementById(id: string): Element {
            return ElementRegistry.elements[id];
        }

    }

}