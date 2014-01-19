module api.content.page.region {

    export class PageRegions {

        private regionByName: {[s:string] : Region;} = {};

        constructor(builder: PageRegionsBuilder) {

            builder.regions.forEach((region: Region) => {
                if (this.regionByName[region.getName()] != undefined) {
                    throw new Error("Regions in a Page must be unique by name, duplicate found: " + region.getName());
                }

                this.regionByName[region.getName()] = region;
            });
        }

        ensureUniqueComponentNameFromTemplate(templateName: api.content.page.TemplateName): api.content.page.ComponentName {

            var wantedName = new api.content.page.ComponentName(templateName.toString());
            return this.ensureUniqueComponentName(wantedName);
        }

        ensureUniqueComponentName(wantedName: api.content.page.ComponentName): api.content.page.ComponentName {

            var region: Region;
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

        getRegions(): Region[] {
            var regions = [];
            for (var i in this.regionByName) {
                var region = this.regionByName[i];
                regions.push(region);
            }
            return regions;
        }

        getRegion(name: string): Region {
            return this.regionByName[name];
        }


        getComponent(name: api.content.page.ComponentName) {

            for (var key in this.regionByName) {
                var region: Region = this.regionByName[key];

                var component = region.getComponent(name);
                if (component != null) {
                    return component;
                }
            }

            return null;
        }

        public toJson(): json.RegionJson[] {

            var regionJsons: json.RegionJson[] = [];
            this.getRegions().forEach((region: Region) => {
                regionJsons.push(region.toJson());
            });
            return regionJsons;
        }
    }

    export class PageRegionsBuilder {

        regions: Region[] = [];

        fromJson(regionsJson: json.RegionJson[]): PageRegionsBuilder {

            regionsJson.forEach((regionJson: json.RegionJson) => {

                var regionBuilder = new RegionBuilder().
                    setName(regionJson.name);

                regionJson.components.forEach((componentJson: api.content.page.json.PageComponentJson) => {
                    var pageComponent = api.content.page.PageComponentFactory.createFromJson(componentJson);
                    regionBuilder.addComponent(pageComponent);
                });


                this.addRegion(regionBuilder.build());
            });
            return this;
        }

        addRegion(value: Region): PageRegionsBuilder {
            this.regions.push(value);
            return this;
        }

        public build(): PageRegions {
            return new PageRegions(this);
        }
    }
}