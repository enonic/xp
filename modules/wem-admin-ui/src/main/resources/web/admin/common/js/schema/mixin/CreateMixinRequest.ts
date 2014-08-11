module api.schema.mixin {

    export class CreateMixinRequest extends MixinResourceRequest<api.schema.mixin.json.MixinJson, Mixin> {

        private name:string;

        private config:string;

        private icon:api.icon.Icon;

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

        setIcon( value:api.icon.Icon ):CreateMixinRequest
        {
            this.icon = value;
            return this;
        }

        getParams():Object {
            return {
                name: this.name,
                config: this.config,
                icon: this.icon != null ? this.icon.toJson() : null
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent( super.getResourcePath(), "create" );
        }

        sendAndParse(): Q.Promise<Mixin> {

            return this.send().then((response: api.rest.JsonResponse<api.schema.mixin.json.MixinJson>) => {
                return this.fromJsonToMixin(response.getResult());
            });
        }

    }
}