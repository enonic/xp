module api.content.page {

    export class AbstractRegions {

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
         *  Add component after target component. Returns true if component was added. Component will not be added if target component was not found.
         */
        addComponentAfter(component: PageComponent, target: ComponentPath): ComponentPath {

            var region = this.getRegionForComponent(target);
            if (region == null) {
                return null;
            }

            var index = region.addComponentAfter(component, target.getLastLevel().getComponentName());
            if (index == -1) {
                return null;
            }
            return component.getPath();
        }

        addComponentFirst(component: PageComponent, regionName: string): ComponentPath {

            var region = this.getRegion(regionName);
            if (region == null) {
                return null;
            }

            return region.addComponentFirst(component);
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

            var regionAndComponent = path.getFirstLevel();
            var region = this.getRegion(regionAndComponent.getRegionName());
            if (region == null) {
                return null;
            }
            var component = region.getComponentByName(regionAndComponent.getComponentName());
            if (component == null) {
                return null;
            }

            if (path.numberOfLevels() == 1) {
                return region;
            }

            api.util.assert(component instanceof layout.LayoutComponent, "Expected LayoutComponent: " + api.util.getClassName(component));
            var layoutComponent = <layout.LayoutComponent>component;
            return layoutComponent.getLayoutRegions().getRegionForComponent(path.removeFirstLevel());
        }

        getRegions(): region.Region[] {
            var regions = [];
            for (var i in this.regionByName) {
                var region = this.regionByName[i];
                regions.push(region);
            }
            return regions;
        }

        getRegion(name: string): region.Region {
            return this.regionByName[name];
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


        getComponent(path: ComponentPath): PageComponent {

            var first: ComponentPathRegionAndComponent = path.getFirstLevel();
            var region = this.getRegion(first.getRegionName());
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

        ensureUniqueComponentNameFromTemplate(templateName: TemplateName): ComponentName {

            var wantedName = new ComponentName(templateName.toString());
            return this.ensureUniqueComponentName(wantedName);
        }

        ensureUniqueComponentName(wantedName: ComponentName): ComponentName {

            var region: region.Region;
            var duplicates = false;
            for (var key in this.regionByName) {
                region = this.regionByName[key];

                if (region.countNumberOfDuplicates(wantedName) > 0) {
                    duplicates = true;
                    break;
                }
            }

            if (!duplicates) {
                return wantedName;
            }

            var instanceCount = 0;
            for (key in this.regionByName) {
                region = this.regionByName[key];
                instanceCount += region.countNumberOfDuplicates(wantedName);
            }

            return wantedName.createDuplicate(instanceCount);
        }

        public toJson(): region.json.RegionJson[] {

            var regionJsons: region.json.RegionJson[] = [];
            this.getRegions().forEach((region: region.Region) => {
                regionJsons.push(region.toJson());
            });
            return regionJsons;
        }
    }

}