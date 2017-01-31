module api {

    export class ClassHelper {

        static MAX_NEST_LEVEL: number = 7;
        static ALLOWED_PACKAGES: string[] = ['api', 'app', 'LiveEdit'];

        /**
         * Returns function name or empty string if function is anonymous.
         *
         * @param func - reference to a function.
         * @returns function name as string.
         */
        // Compiler hack since target version is ES5
        // but the Function.name was presented in ES2015
        static getFunctionName(func: any/*Function*/): string {
            if (func.name) {
                return func.name;
            } else {
                let funcNameRegex = /function (.+)\(/;
                let results = (funcNameRegex).exec(func.toString());
                return (results && results.length > 1) ? results[1] : '';
            }
        }

        /**
         * Returns name of function which was used to create this instance.
         * In case of using typescript it returns typescript class name.
         *
         * @param instance of typescript class.
         * @returns {string} class name.
         */

        static getClassName(instance: any): string {
            return ClassHelper.getFunctionName(instance['constructor']);
        }

        /**
         * Returns function which was used to create this instance.
         * In case of using typescript it returns typescript class.
         *
         * @param instance object
         * @returns {function} class
         */

        static getClass(instance: any): Function {
            return instance['constructor'];
        }

        /**
         * Returns full module path to given object or class.
         *
         * @param instance - reference to class or it's instance.
         * @returns {string} full module name.
         */

        static getModuleName(instance: any): string {
            let fullName = ClassHelper.getFullName(instance);
            return fullName ? fullName.substr(0, fullName.lastIndexOf('.')) : '';
        }

        /**
         * Returns full class name including full path to the class and class name.
         *
         * @param instance - reference to class, its instance or exported function.
         * @returns {string} full class name.
         */
        static getFullName(instance: any): string {
            let constructor = (typeof instance === 'function') ? instance : instance['constructor'];
            //last one expression for IE
            return ClassHelper.findPath(window, constructor) || constructor['name'] ||
                   constructor.toString().match(/^function\s*([^\s(]+)/)[1];
        }

        /**
         * Recursively looks through the objects tree searching for the given constructor function.
         *
         * @param obj - top level object to iterate through its keys.
         * @param constructor - function which is looking for to resolve its path.
         * @param nestLevel - current level of recursive calls.
         * @returns {string} full name included modules and class name.
         */

        static findPath(obj: Object, constructor: Function, nestLevel: number = 1): string {
            let value;
            let path;

            // don't search in current package if nest level is to big
            if (nestLevel > ClassHelper.MAX_NEST_LEVEL) {
                return null;
            }

            // iterate through object keys, check if they contains constructor function
            for (let key in obj) {
                if (obj.hasOwnProperty(key)) {
                    if (nestLevel === 1 && ClassHelper.ALLOWED_PACKAGES.indexOf(key) < 0) {
                        // look into allowed top level packages only or up to max nest level
                        continue;
                    }
                    value = obj[key];
                    // skip nulls and recursive values
                    if (!value || value === obj) {
                        continue;
                    }
                    if (typeof value === 'object') {
                        path = ClassHelper.findPath(value, constructor, nestLevel + 1);
                        if (path) {
                            return `${key}.${path}`;
                        }
                    } else if (typeof value === 'function') {
                        if (value === constructor) {
                            return ClassHelper.getFunctionName(constructor);
                        }
                    }
                }
            }
            return path;
        }

        /**
         * Calculates the number of super classes between given instance and clazz.
         */
        static distanceTo(instance: any, clazz: Function): number {

            if (ClassHelper.getClassName(instance) === ClassHelper.getFunctionName(clazz)) {
                return 0;
            }

            let distance = 0;
            let prototype = Object.getPrototypeOf(instance);
            do {
                prototype = Object.getPrototypeOf(prototype);
                if (!prototype) {
                    return distance;
                }
                distance++;
            }
            while (ClassHelper.getClassName(prototype) !== ClassHelper.getFunctionName(clazz));

            return distance;
        }

    }

}
