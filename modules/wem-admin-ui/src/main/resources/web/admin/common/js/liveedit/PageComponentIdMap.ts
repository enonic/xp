module api.liveedit {

    import ComponentPath2 = api.content.page.ComponentPath2;

    export class PageComponentIdMap {

        private idToPathMap: {[id: string]: ComponentPath2} = {};
        private pathToIdMap: {[path: string]: PageComponentId} = {};

        add(path: ComponentPath2, id: PageComponentId) {
            var idAsString = id.toString();
            api.util.assert(!this.idToPathMap[idAsString], "A PageComponentIdMap can only contain unique id's: " + id);
            this.idToPathMap[ idAsString] = path;
            this.pathToIdMap[path.toString()] = id;
        }

        getPaths(): ComponentPath2[] {
            var paths: ComponentPath2[] = [];
            for (var id in this.idToPathMap) {
                if (this.idToPathMap.hasOwnProperty(id)) {
                    paths.push(this.idToPathMap[id]);
                }
            }
            return paths;
        }

        getIdByPath(path: ComponentPath2): PageComponentId {

            return this.pathToIdMap[path.toString()];
        }

    }
}