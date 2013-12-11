module api_schema_mixin {

    export class CreateMixinRequest extends MixinResourceRequest<api_schema_mixin_json.MixinJson> {

        private name:string;

        private config:string;

        private icon:api_icon.Icon;

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

        setIcon( value:api_icon.Icon ):CreateMixinRequest
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

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent( super.getResourcePath(), "create" );
        }

        sendAndParse(): JQueryPromise<Mixin> {

            var deferred = jQuery.Deferred<Mixin>();

            this.send().done((response: api_rest.JsonResponse<api_schema_mixin_json.MixinJson>) => {
                deferred.resolve(this.fromJsonToMixin(response.getResult()));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }

    }
}