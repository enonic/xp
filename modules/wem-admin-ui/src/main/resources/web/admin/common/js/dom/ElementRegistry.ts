module api.dom {

    export class ElementRegistry {

        private static counters: { [index: string]: number; } = {};

        private static elements: { [index: string]: api.dom.Element; } = {};

        public static registerElement(el: api.dom.Element): string {
            var fullName,
                id = el.getId();

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
            ElementRegistry.elements[id] = el;

            return id;
        }

        public static unregisterElement(el: api.dom.Element) {
            if (el) {
                delete ElementRegistry.elements[el.getId()];
                var fullName = api.util.getFullName(el);
                var count = --ElementRegistry.counters[fullName];
                if (count < 0) {
                    delete ElementRegistry.counters[fullName];
                }
            }
        }

        public static getElementById(id: string): api.dom.Element {
            return ElementRegistry.elements[id];
        }

    }

}