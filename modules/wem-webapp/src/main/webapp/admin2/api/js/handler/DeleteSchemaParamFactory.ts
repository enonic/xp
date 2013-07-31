module api_handler {

    export class DeleteSchemaParamFactory {

        static create(schemes:api_model.SchemaExtModel[]):api_handler.DeleteSchemaParam {

            var schemaNames:string[] = [];
            for (var i = 0; i < schemes.length; i++) {
                schemaNames[i] = schemes[i].data.qualifiedName;
            }

            return  {
                type: schemes.length > 0 ? schemes[0].data.type : undefined,
                qualifiedNames: schemaNames
            };
        }
    }
}