module api.util {

    var MAX_NEST_LEVEL = 7;
    var ALLOWED_PACKAGES = ['api', 'app', 'LiveEdit'];

    export function getFunctionName(func): string {
        if (func.name) {
            return func.name;
        } else {
            var funcNameRegex = /function (.+)\(/;
            var results = (funcNameRegex).exec(func.toString());
            return (results && results.length > 1) ? results[1] : "";
        }
    }

    export function getClassName(instance): string {
        return getFunctionName(instance["constructor"]);
    }

    export function getModuleName(instance): string {
        var fullName = getFullName(instance);
        return fullName ? fullName.substr(0, fullName.lastIndexOf(".")) : "";
    }

    export function getFullName(instance): string {
        var constructor = (typeof instance === 'function') ? instance : instance["constructor"];
        return findPath(window, constructor) || "";
    }

    function findPath(obj: Object, constructor: Function, nestLevel?: number): string {
        var value, path, nestLevel = nestLevel || 1;
        for (var key in obj) {
            if (obj.hasOwnProperty(key)) {
                if (nestLevel == 1 && ALLOWED_PACKAGES.length > 0 && ALLOWED_PACKAGES.indexOf(key) < 0 || nestLevel > MAX_NEST_LEVEL) {
                    // look into allowed top level packaged only or up to max nest level
                    continue;
                }
                value = obj[key];
                if (!value || value == obj) {
                    // skip nulls and recursive values
                    continue;
                }
                if (typeof value === 'object') {
                    path = findPath(value, constructor, nestLevel + 1);
                    if (path) {
                        return key + "." + path;
                    }
                } else if (typeof value === 'function') {
                    if (value == constructor) {
                        return getFunctionName(constructor);
                    }
                }
            }
        }
        return path;
    }

}
