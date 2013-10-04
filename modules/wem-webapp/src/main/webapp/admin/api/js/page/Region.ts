module api_page{

    export class Region {

        private name:string;

        private components:Component[];

        constructor(regionJson:api_page_json.RegionJson) {

            this.name = regionJson.name;

            regionJson.components.forEach((componentJsonJson:api_page_json.ComponentJson) => {
                var component = ComponentFactory.createComponent(componentJsonJson);
                this.components.push(component);
            });
        }

        getName():string {
            return this.name;
        }

        getComponents():Component[] {
            return this.components;
        }

    }
}