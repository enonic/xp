module api.content.page {

    import Region = api.content.page.region.Region;

    export class AbstractRegions implements api.Equitable {

        private regionByName: {[s:string] : region.Region;} = {};

        constructor(regions: region.Region[]) {

            regions.forEach((region: region.Region) => {
                if (this.regionByName[region.getName()] != undefined) {
                    throw new Error("Regions in a Page must be unique by name, duplicate found: " + region.getName());
                }

                this.regionByName[region.getName()] = region;
            });
        }

        addRegion(region: Region) {
            this.regionByName[name] = region;
        }

        removeRegions(regions: Region[]) {
            regions.forEach((region: Region) => {
                delete this.regionByName[region.getName()];
            });
        }


        getRegions(): region.Region[] {
            var regions = [];
            for (var i in this.regionByName) {
                var region = this.regionByName[i];
                regions.push(region);
            }
            return regions;
        }

        getRegionByName(name: string): region.Region {

            return this.regionByName[name];
        }

        getRegionByPath(path: RegionPath): region.Region {

            if (!path.hasParentComponentPath()) {
                return this.getRegionByName(path.getRegionName());
            }
            else {

                var layout = this.getLayoutComponent(path.getParentComponentPath());
                return layout.getLayoutRegions().getRegionByName(path.getRegionName())
            }
        }

        getImageComponent(path: ComponentPath): image.ImageComponent {

            var component = this.getComponent(path);
            if (component == null) {
                return null;
            }
            api.util.assert(api.ObjectHelper.iFrameSafeInstanceOf(component, image.ImageComponent),
                    "PageComponent [" + component.getPath().toString() + "] not an ImageComponent: " +
                    api.ClassHelper.getClassName(component));

            return <image.ImageComponent>component;
        }

        getLayoutComponent(path: ComponentPath): layout.LayoutComponent {

            var component = this.getComponent(path);
            if (component == null) {
                return null;
            }
            api.util.assert(api.ObjectHelper.iFrameSafeInstanceOf(component, layout.LayoutComponent),
                    "PageComponent [" + component.getPath().toString() + "] not an LayoutComponent: " +
                    api.ClassHelper.getClassName(component));

            return <layout.LayoutComponent>component;
        }


        getComponent(path: ComponentPath): Component {

            var first: ComponentPathRegionAndComponent = path.getFirstLevel();
            var region = this.getRegionByName(first.getRegionName());
            var component = region.getComponentByIndex(first.getComponentIndex());

            if (path.numberOfLevels() == 1) {
                return component;
            }
            else {
                if (!api.ObjectHelper.iFrameSafeInstanceOf(component, api.content.page.layout.LayoutComponent)) {
                    throw new Error("Expected component to be a LayoutComponent: " + api.ClassHelper.getClassName(component));
                }

                var layoutComponent = <api.content.page.layout.LayoutComponent> component;
                return layoutComponent.getComponent(path.removeFirstLevel());
            }
        }

        public toJson(): region.RegionJson[] {

            var regionJsons: region.RegionJson[] = [];
            this.getRegions().forEach((region: region.Region) => {
                regionJsons.push(region.toJson());
            });
            return regionJsons;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, AbstractRegions)) {
                return false;
            }

            var other = <AbstractRegions>o;


            var thisRegions = this.getRegions();
            var otherRegions = other.getRegions();

            if (!api.ObjectHelper.arrayEquals(thisRegions, otherRegions)) {
                return false;
            }

            return true;
        }
    }

}