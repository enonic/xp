module api.content.site{

    export class Vendor {

        private name:string;

        private url:string;

        constructor( json:api.content.site.VendorJson ){
            this.name = json.name;
            this.url = json.url;
        }

        getName():string{
            return this.name;
        }

        getUrl():string{
            return this.url;
        }

        toJson(): Object {
            var json = {
                name: this.name,
                url: this.url
            };

            return json;
        }
    }
}