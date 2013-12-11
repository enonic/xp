module api_schema_mixin {

    export class UpdateMixinRequest extends MixinResourceRequest<api_schema_mixin_json.MixinJson> {

        private mixinToUpdate:string;

        private name:string;

        private config:string;

        private icon:api_icon.Icon;

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

        setIcon( value:api_icon.Icon ):UpdateMixinRequest{
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

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent( super.getResourcePath(), "update" );
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