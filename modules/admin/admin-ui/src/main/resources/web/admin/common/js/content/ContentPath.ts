module api.content {

    export class ContentPath implements api.Equitable {

        private static ELEMENT_DIVIDER: string = "/";

        public static ROOT = ContentPath.fromString("/");

        private elements: string[];

        private refString: string;

        public static fromParent(parent: ContentPath, name: string): ContentPath {

            var elements = parent.elements;
            elements.push(name);
            return new ContentPath(elements);
        }

        public static fromString(path: string): ContentPath {

            var elements: string[];

            if (path.indexOf("/") == 0 && path.length > 1) {
                path = path.substr(1);
                elements = path.split(ContentPath.ELEMENT_DIVIDER);
            }
            else if (path == "/") {
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

        getName(): string {
            return this.elements[this.elements.length - 1];
        }

        getLevel(): number {
            return this.elements.length;
        }

        hasParentContent(): boolean {
            return this.elements.length > 1;
        }

        getLastElement(): string {
            return (this.elements[this.elements.length - 1] || "");
        }

        getParentPath(): ContentPath {

            if (this.elements.length < 1) {
                return null;
            }
            var parentElements: string[] = [];
            this.elements.forEach((element: string, index: number)=> {
                if (index < this.elements.length - 1) {
                    parentElements.push(element);
                }
            });
            return new ContentPath(parentElements);
        }

        isRoot(): boolean {
            return this.equals(ContentPath.ROOT);
        }

        isNotRoot(): boolean {
            return !this.equals(ContentPath.ROOT);
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentPath)) {
                return false;
            }

            var other = <ContentPath>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }

        isDescendantOf(path: ContentPath): boolean {
            return (this.refString.indexOf(path.toString()) === 0) && (this.getLevel() > path.getLevel());
        }

        isChildOf(path: ContentPath): boolean {
            return (this.refString.indexOf(path.toString()) === 0) && (this.getLevel() === path.getLevel() + 1);
        }

        prettifyUnnamedPathElements(): ContentPath {

            var prettyElements: string[] = [];
            this.elements.forEach((element: string) => {
                if (ContentName.fromString(element).isUnnamed()) {
                    prettyElements.push("<" + ContentUnnamed.PRETTY_UNNAMED + ">");
                } else {
                    prettyElements.push(element);
                }
            });

            return new ContentPath(prettyElements);
        }

        toString() {
            return this.refString;
        }
    }
}