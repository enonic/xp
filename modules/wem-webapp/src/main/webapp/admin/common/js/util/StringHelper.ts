module api_util {

    export function limitString(str:string, length:number, ending:string = "..."):string {
        str = str.substring(0, length) + ending;
        return str;
    }

    export function isStringEmpty(str:string) {
        if(!str) {
            return null;
        }

        return str.length == 0;
    }
}
