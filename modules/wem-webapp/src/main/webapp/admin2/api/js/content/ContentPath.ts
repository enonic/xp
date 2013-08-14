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
            var spacePrefixDividerPos:number = s.indexOf(ContentPath.SPACE_PREFIX_DIVIDER);
            var absolute:bool = spacePrefixDividerPos > -1;
            if (absolute) {
                path = s.substr(spacePrefixDividerPos + 1, s.length);
                spaceName = s.substr(0, spacePrefixDividerPos);
            }
            else {
                path = s;
                spaceName = null;
            }

            if( path.indexOf("/") == 0 ) {
                path = path.substr( 1 );
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

        getElements():string[] {
            return this.elements;
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