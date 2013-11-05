module api_schema_mixin {

    export class UpdateMixinRequest extends MixinResourceRequest<any> {

        private mixinToUpdate:string;

        private name:string;

        private config:string;

        private iconReference:string;

        constructor() {
            super();
            super.setMethod( "POST" );
        }

        setMixinToUpdate( mixinToUpdate:string ):UpdateMixinRequest{
            this.mixinToUpdate = mixinToUpdate;
            return this;
        }

        setName( name:string ):UpdateMixinRequest {
            this.name = name;
            return this;
        }

        setConfig( config:string ):UpdateMixinRequest {
            this.config = config;
            return this;
        }

        setIconReference( value:string ):UpdateMixinRequest{
            this.iconReference = value;
            return this;
        }

        getParams():Object {
            return {
                mixinToUpdate: this.mixinToUpdate,
                name: this.name,
                config: this.config,
                iconReference: this.iconReference
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent( super.getResourcePath(), "update" );
        }

    }
}