module api_schema_mixin {

    export class MixinUpdatedEvent extends api_event.Event {

        private mixinName:string;

        constructor( name:string ) {
            super( 'MixinUpdatedEvent' );
            this.mixinName = name;
        }

        public getMixinName():string {
            return this.mixinName;
        }

        static on( handler:( event:MixinUpdatedEvent ) => void ) {
            api_event.onEvent( 'MixinUpdatedEvent', handler );
        }
    }

}