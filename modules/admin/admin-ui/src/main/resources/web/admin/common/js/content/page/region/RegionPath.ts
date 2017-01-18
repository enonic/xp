module api.content.page.region {

    export class RegionPath implements api.Equitable {

        private static DIVIDER: string = '/';

        private parentComponentPath: ComponentPath;

        private regionName: string;

        private refString: string;

        constructor(parentComponentPath: ComponentPath, regionName: string) {

            this.parentComponentPath = parentComponentPath;
            this.regionName = regionName;
            if (parentComponentPath != null) {
                this.refString = parentComponentPath + '/' + regionName;
            } else {
                this.refString = regionName;
            }
        }

        public hasParentComponentPath(): boolean {

            return this.parentComponentPath != null;
        }

        public getParentComponentPath(): ComponentPath {
            return this.parentComponentPath;
        }

        public getRegionName(): string {
            return this.regionName;
        }

        public toString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, RegionPath)) {
                return false;
            }

            let other = <RegionPath>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }

        public static fromString(str: string): RegionPath {

            let lastDivider = str.lastIndexOf(RegionPath.DIVIDER);
            if (lastDivider === -1) {
                return new RegionPath(null, str);
            }

            let regionNameStart = lastDivider + 1;

            let regionName = str.substring(regionNameStart, str.length);
            let componentPathAsString = str.substring(0, regionNameStart);
            let parentPath = ComponentPath.fromString(componentPathAsString);
            return new RegionPath(parentPath, regionName);
        }
    }
}
