module api.content.page {

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

        /*
         *  Add component after precedingComponent in given region. Returns null if region was not found.
         *  Adds component first in region if preceding component is null.
         */
        addComponentAfter(component: PageComponent, regionPath: RegionPath, precedingComponent: ComponentPath): ComponentPath {

            var region = this.getRegionByPath(regionPath);
            if (region == null) {
                return null;
            }

            if (precedingComponent == null) {
                region.addComponentAfter(component, null);
            }
            else {
                var index = region.addComponentAfter(component, precedingComponent.getLastLevel().getComponentName());
                if (index == -1) {
                    return null;
                }
            }

            return component.getPath();
        }

        moveComponent(componentPath: ComponentPath, toRegion: RegionPath, precedingComponent: ComponentPath): ComponentPath {

            var component = this.removeComponent(componentPath);
            if (component) {
                return this.addComponentAfter(component, toRegion, precedingComponent);
            } else {
                return null;
            }

        }

        removeComponent(componentPath: ComponentPath) {
            var componentToBeRemoved = this.getComponent(componentPath);
            var region = this.getRegionForComponent(componentPath);
            return region.removeComponent(componentToBeRemoved);
        }

        hasComponent(name: ComponentName): boolean {

            for (var key in this.regionByName) {
                var region = this.regionByName[key];
                if (region.hasComponentWithName(name)) {
                    return true;
                }
            }
            return false;
        }

        getRegionForComponent(path: ComponentPath): region.Region {

            return this.getRegionByPath(path.getRegionPath());
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
            api.util.assert(component instanceof image.ImageComponent,
                    "PageComponent [" + component.getPath().toString() + "] not an ImageComponent: " + api.util.getClassName(component));

            return <image.ImageComponent>component;
        }

        getLayoutComponent(path: ComponentPath): layout.LayoutComponent {

            var component = this.getComponent(path);
            if (component == null) {
                return null;
            }
            api.util.assert(component instanceof layout.LayoutComponent,
                    "PageComponent [" + component.getPath().toString() + "] not an LayoutComponent: " + api.util.getClassName(component));

            return <layout.LayoutComponent>component;
        }


        getComponent(path: ComponentPath): PageComponent {

            var first: ComponentPathRegionAndComponent = path.getFirstLevel();
            var region = this.getRegionByName(first.getRegionName());
            var component = region.getComponentByName(first.getComponentName());

            if (path.numberOfLevels() == 1) {
                return component;
            }
            else {
                if (!( component instanceof api.content.page.layout.LayoutComponent )) {
                    throw new Error("Expected component to be a LayoutComponent: " + api.util.getClassName(component));
                }

                var layoutComponent = <api.content.page.layout.LayoutComponent> component;
                return layoutComponent.getComponent(path.removeFirstLevel());
            }
        }

        ensureUniqueComponentName(inRegion: RegionPath, wantedName: ComponentName): ComponentName {

            var region = this.getRegionByPath(inRegion);
            return region.ensureUniqueComponentName(wantedName);
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