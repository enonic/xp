module api.schema {

    export class SchemaIconUrlResolver extends api.icon.IconUrlResolver<SchemaIconUrlResolver,Schema> {

        public getResourcePath(): api.rest.Path {
            return api.rest.Path.fromString("schema/image" );
        }

        public resolve(icon: api.icon.Icon): string {

            return this.getResourcePath().toString() + "/" + icon.getBlobKey() + "?" + this.resolveQueryParams();
        }
    }
}