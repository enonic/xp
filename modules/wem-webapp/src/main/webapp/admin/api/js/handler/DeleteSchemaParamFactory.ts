module api_handler {

    export class DeleteSchemaParamFactory {

        static create(schemes:api_schema.Schema[]):api_handler.DeleteSchemaParam {

            var schemaNames:string[] = [];
            for (var i = 0; i < schemes.length; i++) {
                schemaNames[i] = schemes[i].getSchemaName();
            }

            return  {
                type: schemes.length > 0 ? schemes[0].getSchemaType() : undefined,
                qualifiedNames: schemaNames
            };
        }
    }
}