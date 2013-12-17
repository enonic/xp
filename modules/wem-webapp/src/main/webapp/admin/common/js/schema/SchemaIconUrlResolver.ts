module api_schema {

    export class SchemaIconUrlResolver extends api_icon.IconUrlResolver<SchemaIconUrlResolver,Schema> {

        public getResourcePath(): api_rest.Path {
            return api_rest.Path.fromString("schema/image" );
        }

        public resolve(icon: api_icon.Icon): string {

            return this.getResourcePath().toString() + "/" + icon.getBlobKey() + "?" + this.resolveQueryParams();
        }
    }
}