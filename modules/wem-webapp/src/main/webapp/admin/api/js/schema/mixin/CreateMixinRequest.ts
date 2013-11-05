module api_schema_mixin {

    export class CreateMixinRequest extends MixinResourceRequest<any> {

        private name:string;

        private config:string;

        private iconReference:string;

        constructor() {
            super();
            super.setMethod( "POST" );
        }

        setName( name:string ):CreateMixinRequest {
            this.name = name;
            return this;
        }

        setConfig( config:string ):CreateMixinRequest {
            this.config = config;
            return this;
        }

        setIconReference( value:string ):CreateMixinRequest
        {
            this.iconReference = value;
            return this;
        }

        getParams():Object {
            return {
                name: this.name,
                config: this.config,
                iconReference: this.iconReference
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent( super.getResourcePath(), "create" );
        }

    }
}