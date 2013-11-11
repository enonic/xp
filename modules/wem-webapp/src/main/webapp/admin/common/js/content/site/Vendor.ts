module api_content_site{

    export class Vendor {

        private name:string;

        private url:string;

        constructor( json:api_content_site_json.VendorJson ){
            this.name = json.name;
            this.url = json.url;
        }

        getName():string{
            return this.name;
        }

        getUrl():string{
            return this.url;
        }
    }
}