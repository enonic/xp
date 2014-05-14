module api.liveedit {

    import Body = api.dom.Body;
    import PageComponentType = api.content.page.PageComponentType;
    import ComponentPath2 = api.content.page.ComponentPath2;
    import RegionPath2 = api.content.page.RegionPath2;

    export class PageComponentIdMapResolver {

        private counter: number;

        private body: Body;

        private map: PageComponentIdMap;

        constructor(body: Body) {
            this.body = body;
        }

        resolve(): PageComponentIdMap {

            this.counter = 0;
            this.map = new PageComponentIdMap();
            this.parseRegions(this.body);

            return this.map;
        }

        private parseRegions(parent: api.dom.Element, componentPath?: ComponentPath2) {

            var regionIndex: number = 0;
            var children = parent.getChildren();
            children.forEach((element: api.dom.Element) => {
                var type = element.getEl().getData("live-edit-type");
                if (type == "region") {
                    this.parsePageComponents(element, new RegionPath2(componentPath, regionIndex++));
                }
            });
        }

        private parsePageComponents(parent: api.dom.Element, region: RegionPath2) {

            var componentIndex = 0;
            var children = parent.getChildren();
            children.forEach((element: api.dom.Element) => {
                var type = element.getEl().getData("live-edit-type");
                if (PageComponentType.byShortName(type)) {
                    var path = ComponentPath2.fromRegionPathAndComponentIndex(region, componentIndex++);
                    this.map.add(path, this.counter++);
                    if (type == 'layout') {
                        this.parseRegions(element, path);
                    }
                }
            });
        }
    }
}