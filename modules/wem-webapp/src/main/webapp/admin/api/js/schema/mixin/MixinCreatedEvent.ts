module api_schema_mixin {

    export class MixinCreatedEvent extends api_event.Event {

        private mixinName:string;

        constructor( name:string ) {
            super( 'MixinCreatedEvent' );
            this.mixinName = name;
        }

        public getMixinName():string{
            return this.mixinName;
        }

        static on( handler:( event:MixinCreatedEvent ) => void ) {
            api_event.onEvent( 'MixinCreatedEvent', handler );
        }
    }

}