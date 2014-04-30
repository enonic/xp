module api.content.site {

    export class Vendor implements api.Equitable {

        private name: string;

        private url: string;

        constructor(json: api.content.site.VendorJson) {
            this.name = json.name;
            this.url = json.url;
        }

        getName(): string {
            return this.name;
        }

        getUrl(): string {
            return this.url;
        }

        toJson(): Object {
            var json = {
                name: this.name,
                url: this.url
            };

            return json;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Vendor)) {
                return false;
            }

            var other = <Vendor>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.url, other.url)) {
                return false;
            }

            return true;
        }
    }
}