module api_data{

    /*
     * Types need to be named as in ValueTypes.java
     */
    export class ValueTypes {

        static DATA = new ValueType( "Data" );

        static STRING = new ValueType("String");

        static DATE_TIME = new ValueType("DateTime");

        static ATTACHMENT_NAME = new ValueType("AttachmentName");

        static CONTENT_ID = new ValueType("ContentId");

        static REGION = new ValueType("Region");

        static ALL:ValueType[] = [ ValueTypes.DATA, ValueTypes.STRING, ValueTypes.DATE_TIME, ValueTypes.ATTACHMENT_NAME,
            ValueTypes.CONTENT_ID, ValueTypes.REGION
        ];

        public static fromName( name:string ):ValueType
        {
            var match = null;
            ValueTypes.ALL.forEach( (valueType:ValueType) => {
                if( valueType.toString() == name ) {
                    match = valueType;
                }
            } );

            return match;
        }
    }
}