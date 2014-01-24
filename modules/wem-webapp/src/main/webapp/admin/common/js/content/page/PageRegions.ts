module api.content.page {

    export class PageRegions {

        private regionByName: {[s:string] : region.Region;} = {};

        constructor(builder: PageRegionsBuilder) {

            builder.regions.forEach((region: region.Region) => {
                if (this.regionByName[region.getName()] != undefined) {
                    throw new Error("Regions in a Page must be unique by name, duplicate found: " + region.getName());
                }

                this.regionByName[region.getName()] = region;
            });
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

        addComponent(component: api.content.page.PageComponent<TemplateKey>, regionName: string) {

            //var regionForComponent = this.getRegionForComponent(component.getName());
            //api.util.assert(regionForComponent != null, "Component already added to region [" + regionForComponent.getName() + "]: " + component.getName().toString());

            var region = this.getRegion(regionName);
            region.addComponent(component);
        }

        hasComponent(name: api.content.page.ComponentName): boolean {

            for (var key in this.regionByName) {
                var region = this.regionByName[key];
                if (region.hasComponentWithName(name)) {
                    return true;
                }
            }
            return false;
        }

        getRegionForComponent(name: api.content.page.ComponentName): region.Region {

            for (var key in this.regionByName) {
                var region = this.regionByName[key];
                if (region.hasComponentWithName(name)) {
                    return region;
                }
            }
            return null;
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


        getComponent(name: ComponentName) {

            for (var key in this.regionByName) {
                var region: region.Region = this.regionByName[key];

                var component = region.getComponent(name);
                if (component != null) {
                    return component;
                }
            }

            return null;
        }

        public toJson(): region.json.RegionJson[] {

            var regionJsons: region.json.RegionJson[] = [];
            this.getRegions().forEach((region: region.Region) => {
                regionJsons.push(region.toJson());
            });
            return regionJsons;
        }
    }

    export class PageRegionsBuilder {

        regions: region.Region[] = [];

        fromJson(regionsJson: region.json.RegionJson[]): PageRegionsBuilder {

            regionsJson.forEach((regionJson: region.json.RegionJson) => {

                var regionBuilder = new region.RegionBuilder().
                    setName(regionJson.name);

                regionJson.components.forEach((componentJson: json.PageComponentTypeWrapperJson) => {
                    var pageComponent = PageComponentFactory.createFromJson(componentJson);
                    regionBuilder.addComponent(pageComponent);
                });


                this.addRegion(regionBuilder.build());
            });
            return this;
        }

        addRegion(value: region.Region): PageRegionsBuilder {
            this.regions.push(value);
            return this;
        }

        public build(): PageRegions {
            return new PageRegions(this);
        }
    }
}