module api_util {

    export function getClassName(instance) {
        var funcNameRegex = /function (.+)\(/;
        var results = (funcNameRegex).exec(instance["constructor"].toString());
        return (results && results.length > 1) ? results[1] : "";
    }

}
