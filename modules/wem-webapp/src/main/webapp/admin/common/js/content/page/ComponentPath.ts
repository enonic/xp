module api.content.page {

    export class ComponentPath {

        private static COMPONENT_TYPE_SEPARATOR = ":";

        private static PATH_ELEMENT_DIVIDER = "/";

        private componentType: string;

        private pathElements: string[] = [];

        private refString: string;

        constructor(componentType: string, pathElements: string[]) {
            this.componentType = componentType;
            this.pathElements = pathElements;

            this.refString = this.componentType + ComponentPath.COMPONENT_TYPE_SEPARATOR;
            this.pathElements.forEach((element: string, index: number) => {
                this.refString += element;
                if (index < this.pathElements.length - 2) {
                    this.refString += ComponentPath.PATH_ELEMENT_DIVIDER;
                }
            });
        }

        getComponentType(): string {
            return this.componentType;
        }

        public toString(): string {
            return this.refString;
        }

        public static fromString(str: string): ComponentPath {

            api.util.assert(str.indexOf(ComponentPath.COMPONENT_TYPE_SEPARATOR) != -1,
                "Missing COMPONENT_TYPE_SEPARATOR (" + ComponentPath.COMPONENT_TYPE_SEPARATOR + ") in ComponentPath: " + str);
            var pathStart = str.indexOf(ComponentPath.COMPONENT_TYPE_SEPARATOR);
            var componentType = str.substring(0, pathStart);

            var pathAsString = str.substring(pathStart, str.length);
            var elements: string[] = pathAsString.split(ComponentPath.PATH_ELEMENT_DIVIDER);
            elements = ComponentPath.removeEmptyElements(elements);
            return new ComponentPath(componentType, elements);
        }

        private static removeEmptyElements(elements: string[]): string[] {
            var filteredElements: string[] = [];
            elements.forEach((element: string) => {
                if (element.length > 0) {
                    filteredElements.push(element);
                }
            });
            return filteredElements;
        }
    }
}