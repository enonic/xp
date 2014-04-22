/**
 * @fileoverview
 *
 * This module provides a familiar console object for logging and debugging.
 *
 * @namespace console
 */
var console = (function () {

    return {

        /**
         * Logs a message to the console.
         *
         * The first argument to log may be a string containing printf-like placeholders.
         * Otherwise, multiple arguments will be concatenated separated by spaces.
         *
         * @param msg... one or more message arguments
         * @function console.log
         */
        log: function () {

        },

        /**
         * Logs a message with the visual "info" representation, including the file name
         * and line number of the calling code.
         *
         * @param msg... one or more message arguments
         * @function console.info
         */
        info: function () {

        },

        /**
         * Logs a message with the visual "warn" representation, including the file name
         * and line number of the calling code.
         *
         * @param msg... one or more message arguments
         * @function console.warn
         */
        warn: function () {

        },

        /**
         * Logs a message with the visual "error" representation, including the file name
         * and line number of the calling code.
         *
         * @param msg... one or more message arguments
         * @function console.error
         */
        error: function () {

        },

        /**
         * Prints a stack trace of JavaScript execution at the point where it is called.
         *
         * @param msg... optional message arguments
         * @function console.trace
         */
        trace: function () {

        }

    };

})();

