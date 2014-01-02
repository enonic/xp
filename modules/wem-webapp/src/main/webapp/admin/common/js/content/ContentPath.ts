module api.content {

    export class ContentPath {

        private static ELEMENT_DIVIDER: string = "/";

        public static ROOT = ContentPath.fromString("/");

        private elements: string[];

        private refString: string;

        public static fromString(path: string) {

            var elements: string[];

            if (path.indexOf("/") == 0 && path.length > 1) {
                path = path.substr(1);
                elements = path.split(ContentPath.ELEMENT_DIVIDER);
            }
            else if( path == "/" ) {
                elements = [];
            }

            return new ContentPath(elements);
        }

        constructor(elements: string[]) {
            this.elements = elements;
            if (elements.length == 0) {
                this.refString = ContentPath.ELEMENT_DIVIDER;
            }
            else {
                this.refString = ContentPath.ELEMENT_DIVIDER + this.elements.join(ContentPath.ELEMENT_DIVIDER);
            }
        }

        getElements(): string[] {
            return this.elements;
        }

        hasParentContent(): boolean {
            return this.elements.length > 1;
        }

        getParentPath(): ContentPath {

            if (this.elements.length < 1) {
                return null;
            }
            var parentElemements: string[] = [];
            this.elements.forEach((element: string, index: number)=> {
                if (index < this.elements.length - 1) {
                    parentElemements.push(element);
                }
            });
            return new ContentPath(parentElemements);
        }

        toString() {
            return this.refString;
        }
    }
}