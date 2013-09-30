module api_data{

    /*
     * Types need to be named as in ValueTypes.java
     */
    export class ValueTypes {

        static TEXT = new ValueType("Text");

        static DATE_TIME = new ValueType("DateTime");

        static ALL:ValueType[] = [ ValueTypes.TEXT, ValueTypes.DATE_TIME ];

        static fromName(name:string):ValueType {
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