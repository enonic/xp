module api_util {

    export function limitString(str:string, length:number, ending:string = "..."):string {
        str = str.substring(0, length) + ending;
        return str;
    }

    export function isStringEmpty(str:string) {
        if(!str) {
            return true;
        }

        return str.length == 0;
    }

    export function isStringBlank(str:string) {

        return (!str || /^\s*$/.test(str));
    }
}
