module api.content.page {

    export class RegionPath2 implements api.Equitable {

        private static DIVIDER = "/";

        private parentComponentPath: ComponentPath2;

        private regionIndex: number;

        private refString: string;

        constructor(parentComponentPath: ComponentPath2, regionIndex: number) {

            this.parentComponentPath = parentComponentPath;
            this.regionIndex = regionIndex;
            if (parentComponentPath != null) {
                this.refString = parentComponentPath + "/region[" + regionIndex + "]";
            }
            else {
                this.refString = "region[" + regionIndex + "]";
            }
        }

        public hasParentComponentPath(): boolean {

            return this.parentComponentPath != null;
        }

        public getParentComponentPath(): ComponentPath2 {
            return this.parentComponentPath;
        }

        public getRegionIndex(): number {
            return this.regionIndex;
        }

        public toString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, RegionPath)) {
                return false;
            }

            var other = <RegionPath2>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }

        public static fromString(str: string): RegionPath2 {

            var lastDivider = str.lastIndexOf(RegionPath2.DIVIDER);
            if (lastDivider == -1) {
                return new RegionPath2(null, 0);
            }

            var regionStart = lastDivider + 1;

            var coponentPathAsString = str.substring(0, regionStart);
            var parentPath = ComponentPath2.fromString(coponentPathAsString);

            var endsWithEndBracket: boolean = str.indexOf(']', str.length - ']'.length) !== -1;
            var containsStartBracket: boolean = str.indexOf('[') !== -1;

            if (endsWithEndBracket && containsStartBracket) {
                var firstBracketPos: number = str.indexOf('[');
                var nameStr: string = str.substring(0, firstBracketPos);
                var indexStr: string = str.substring(nameStr.length + 1, (str.length - 1));
                var index: number = parseInt(indexStr);

                return new RegionPath2(parentPath, index);
            }
            else {

                return new RegionPath2(parentPath, 0);
            }
        }
    }
}