module api.util {

    var MAX_NEST_LEVEL = 7;
    var ALLOWED_PACKAGES = ['api', 'app', 'LiveEdit'];

    /**
     * Returns function name or empty string if function is anonymous.
     *
     * @param func - reference to a function.
     * @returns function name as string.
     */
    export function getFunctionName(func): string {
        if (func.name) {
            return func.name;
        } else {
            var funcNameRegex = /function (.+)\(/;
            var results = (funcNameRegex).exec(func.toString());
            return (results && results.length > 1) ? results[1] : "";
        }
    }

    /**
     * Returns name of function which was used to create this instance.
     * In case of using typescript it returns typescript class name.
     *
     * @param instance of typescript class.
     * @returns {string} class name.
     */
    export function getClassName(instance): string {
        return getFunctionName(instance["constructor"]);
    }

    /**
     * Returns function which was used to ceate this instance.
     * In case of using typescript it returns typescript class.
     *
     * @param instance object
     * @returns {function} class
     */
    export function getClass(instance: any): Function {
        return instance["constructor"];
    }

    /**
     * Returns full module path to given object or class.
     *
     * @param instance - reference to class or it's instance.
     * @returns {string} full module name.
     */
    export function getModuleName(instance): string {
        var fullName = getFullName(instance);
        return fullName ? fullName.substr(0, fullName.lastIndexOf(".")) : "";
    }

    /**
     * Returns full class name including full path to the class and class name.
     *
     * @param instance - reference to class, its instance or exported function.
     * @returns {string} full class name.
     */
    export function getFullName(instance): string {
        var constructor = (typeof instance === 'function') ? instance : instance["constructor"];
        return findPath(window, constructor) || "";
    }

    /**
     * Recursively looks through the objects tree searching for the given constructor function.
     *
     * @param obj - top level object to iterate through its keys.
     * @param constructor - function which is looking for to resolve its path.
     * @param nestLevel - current level of recursive calls.
     * @returns {string} full name included modules and class name.
     */
    function findPath(obj: Object, constructor: Function, nestLevel?: number): string {
        var value, path, nestLevel = nestLevel || 1;

        // don't search in current package if nest level is to big
        if (nestLevel > MAX_NEST_LEVEL) {
            return null;
        }

        // iterate through object keys, check if they contains constructor function
        for (var key in obj) {
            if (obj.hasOwnProperty(key)) {
                if (nestLevel == 1 && ALLOWED_PACKAGES.indexOf(key) < 0) {
                    // look into allowed top level packages only or up to max nest level
                    continue;
                }
                value = obj[key];
                // skip nulls and recursive values
                if (!value || value == obj) {
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
