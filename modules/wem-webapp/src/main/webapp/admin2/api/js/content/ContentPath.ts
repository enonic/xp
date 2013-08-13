module api_content{

    export class ContentPath {

        private static SPACE_PREFIX_DIVIDER:string = ":";

        private static ELEMENT_DIVIDER:string = "/";

        private spaceName:string;

        private elements:string[];

        private refString:string;

        public static fromString(s:string) {

            var spaceName:string;
            var path:string;
            var absolute:bool = s.indexOf(ContentPath.SPACE_PREFIX_DIVIDER) > -1;
            if (absolute) {
                path = s.substr(s.indexOf(ContentPath.SPACE_PREFIX_DIVIDER), s.length);
                spaceName = s.substr(0, s.indexOf(ContentPath.SPACE_PREFIX_DIVIDER));
            }
            else {
                path = s;
                spaceName = null;
            }

            var elements:string[] = path.split(ContentPath.ELEMENT_DIVIDER);
            return new ContentPath(spaceName, elements);
        }

        constructor(spaceName:string, elements:string[]) {
            this.spaceName = spaceName;
            this.elements = elements;

            var spacePrefix:string = this.spaceName == null ? "" : this.spaceName + ContentPath.SPACE_PREFIX_DIVIDER;
            this.refString = spacePrefix + ContentPath.ELEMENT_DIVIDER + this.elements.join(ContentPath.ELEMENT_DIVIDER);
        }

        getSpaceName() {
            return this.spaceName;
        }

        getParentPath():ContentPath {

            if (this.elements.length < 2) {
                return null;
            }
            var parentElemements:string[] = [];
            this.elements.forEach((element:string, index:number)=> {
                if (index < this.elements.length - 1) {
                    parentElemements.push(element);
                }
            });
            return new ContentPath(this.spaceName, parentElemements);
        }

        toString() {
            return this.refString;
        }
    }
}