module api.content {

    export class ContentPath implements api.Equitable {

        public static ELEMENT_DIVIDER: string = '/';

        public static ROOT: ContentPath = ContentPath.fromString('/');

        private elements: string[];

        private refString: string;

        public static fromParent(parent: ContentPath, name: string): ContentPath {

            let elements = parent.elements;
            elements.push(name);
            return new ContentPath(elements);
        }

        public static fromString(path: string): ContentPath {

            let elements: string[];

            if (path.indexOf('/') == 0 && path.length > 1) {
                path = path.substr(1);
                elements = path.split(ContentPath.ELEMENT_DIVIDER);
            } else if (path == '/') {
                elements = [];
            }

            return new ContentPath(elements);
        }

        constructor(elements: string[]) {
            this.elements = elements;
            if (elements.length == 0) {
                this.refString = ContentPath.ELEMENT_DIVIDER;
            } else {
                this.refString = ContentPath.ELEMENT_DIVIDER + this.elements.join(ContentPath.ELEMENT_DIVIDER);
            }
        }

        getPathAtLevel(level: number): ContentPath {
            let result = '';
            for (let index = 0; index < this.getElements().length; index++) {
                result = result + ContentPath.ELEMENT_DIVIDER + this.getElements()[index];
                if (index == (level - 1)) {
                    return ContentPath.fromString(result);
                }
            }
            return null;
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

        getFirstElement(): string {
            return (this.elements[0] || '');
        }

        getParentPath(): ContentPath {

            if (this.elements.length < 1) {
                return null;
            }
            let parentElements: string[] = [];
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

            let other = <ContentPath>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }

        isDescendantOf(path: ContentPath): boolean {
            return (path.isRoot() || this.refString.indexOf(path.toString() + ContentPath.ELEMENT_DIVIDER) === 0) &&
                   (this.getLevel() > path.getLevel());
        }

        isChildOf(path: ContentPath): boolean {
            return (path.isRoot() || this.refString.indexOf(path.toString() + ContentPath.ELEMENT_DIVIDER) === 0) &&
                   (this.getLevel() === path.getLevel() + 1);
        }

        prettifyUnnamedPathElements(): ContentPath {

            let prettyElements: string[] = [];
            this.elements.forEach((element: string) => {
                if (ContentName.fromString(element).isUnnamed()) {
                    prettyElements.push('<' + ContentUnnamed.PRETTY_UNNAMED + '>');
                } else {
                    prettyElements.push(element);
                }
            });

            return new ContentPath(prettyElements);
        }

        toString(): string {
            return this.refString;
        }
    }
}
