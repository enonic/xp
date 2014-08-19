module api.schema {

    export class SchemaIconUrlResolver extends api.icon.IconUrlResolver {

        resolve(schema: Schema) {

            return schema.getIconUrl();
        }

        public static getResourcePath(): api.rest.Path {
            return api.rest.Path.fromString("schema/icon");
        }
    }
}