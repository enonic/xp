module api.schema.mixin {

    export class UpdateMixinRequest extends MixinResourceRequest<api.schema.mixin.json.MixinJson, Mixin> {

        private mixinToUpdate:string;

        private name:string;

        private config:string;

        private icon:api.icon.Icon;

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

        setIcon( value:api.icon.Icon ):UpdateMixinRequest{
            this.icon = value;
            return this;
        }

        getParams():Object {
            return {
                mixinToUpdate: this.mixinToUpdate,
                name: this.name,
                config: this.config,
                icon: this.icon != null ? this.icon.toJson() : null
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent( super.getResourcePath(), "update" );
        }


        sendAndParse(): wemQ.Promise<Mixin> {

            return this.send().then((response: api.rest.JsonResponse<api.schema.mixin.json.MixinJson>) => {
                return this.fromJsonToMixin(response.getResult());
            });
        }
    }
}