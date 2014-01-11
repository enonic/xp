module api.util {

    var MAX_NEST_LEVEL = 5;
    var ALLOWED_PACKAGES = ['api', 'app'];

    export function getClassName(instance) {
        var funcNameRegex = /function (.+)\(/;
        var results = (funcNameRegex).exec(instance["constructor"].toString());
        return (results && results.length > 1) ? results[1] : "";
    }

    // works after all classes are present in document
    export function getModuleName(instance) {
        var className = getClassName(instance);
        return findPath(window, className) || "";
    }

    function findPath(obj: Object, node: string, nestLevel?:number): string {
        var value, path, nestLevel = nestLevel || 1;
        for (var key in obj) {
            if (obj.hasOwnProperty(key)) {
                if(nestLevel == 1 && ALLOWED_PACKAGES.length > 0 && ALLOWED_PACKAGES.indexOf(key) < 0) {
                    // look into allowed top level packaged only
                    continue;
                }
                value = obj[key];
                if (!value || value == obj) {
                    // skip nulls and recursive values
                    continue;
                }
                if (typeof value === 'object' && nestLevel < MAX_NEST_LEVEL) {
                    path = findPath(value, node, nestLevel + 1);
                    if (path) {
                        return key + "." + path;
                    }
                } else if (typeof value === 'function' && value.name == node) {
                    return value.name;
                }
            }
        }
        return path;
    }

}
