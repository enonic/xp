// Type definitions for Hasher
// Project: https://github.com/millermedeiros/Hasher/

/// <reference path="js-signals.d.ts" />

declare module HasherJs {

    export interface HasherJsStatic {

        /**
         * hasher Version Number
         */
            VERSION : string;

        /**
         * String that should always be added to the end of Hash value.
         * <ul>
         * <li>default value: '';</li>
         * <li>will be automatically removed from `hasher.getHash()`</li>
         * <li>avoid conflicts with elements that contain ID equal to hash value;</li>
         * </ul>
         */
            appendHash : string;

        /**
         * String that should always be added to the beginning of Hash value.
         * <ul>
         * <li>default value: '/';</li>
         * <li>will be automatically removed from `hasher.getHash()`</li>
         * <li>avoid conflicts with elements that contain ID equal to hash value;</li>
         * </ul>
         */
            prependHash : string;

        /**
         * String used to split hash paths; used by `hasher.getHashAsArray()` to split paths.
         * <ul>
         * <li>default value: '/';</li>
         * </ul>
         */
            separator : string;

        /**
         * Signal dispatched when hash value changes.
         * - pass current hash as 1st parameter to listeners and previous hash value as 2nd parameter.
         */
            changed : Signal;

        /**
         * Signal dispatched when hasher is stopped.
         * -  pass current hash as first parameter to listeners
         */
            stopped : Signal;

        /**
         * Signal dispatched when hasher is initialized.
         * - pass current hash as first parameter to listeners.
         */
            initialized : Signal;

        /**
         * Start listening/dispatching changes in the hash/history.
         * <ul>
         *   <li>hasher won't dispatch CHANGE events by manually typing a new value or pressing the back/forward buttons before calling this method.</li>
         * </ul>
         */
        init();

        /**
         * Stop listening/dispatching changes in the hash/history.
         * <ul>
         *   <li>hasher won't dispatch CHANGE events by manually typing a new value or pressing the back/forward buttons after calling this method, unless you call hasher.init() again.</li>
         *   <li>hasher will still dispatch changes made programatically by calling hasher.setHash();</li>
         * </ul>
         */
        stop();

        /**
         * If hasher is listening to changes on the browser history and/or hash value.
         */
        isActive(): boolean;

        /**
         * Full URL.
         */
        getURL(): string;

        /**
         * Retrieve URL without query string and hash.
         */
        getBaseURL(): string;

        /**
         * Set Hash value, generating a new history record.
         * @param {...string} path    Hash value without '#'. Hasher will join
         * path segments using `hasher.separator` and prepend/append hash value
         * with `hasher.appendHash` and `hasher.prependHash`
         * @example hasher.setHash('lorem', 'ipsum', 'dolor') -> '#/lorem/ipsum/dolor'
         */
        setHash(path:string);

        /**
         * Set Hash value without keeping previous hash on the history record.
         * Similar to calling `window.location.replace("#/hash")` but will also work on IE6-7.
         * @param {...string} path    Hash value without '#'. Hasher will join
         * path segments using `hasher.separator` and prepend/append hash value
         * with `hasher.appendHash` and `hasher.prependHash`
         * @example hasher.replaceHash('lorem', 'ipsum', 'dolor') -> '#/lorem/ipsum/dolor'
         */
        replaceHash(path:string);

        /**
         * Hash value without '#', `hasher.appendHash` and `hasher.prependHash`.
         */
        getHash ():string;

        /**
         * Hash value split into an Array.
         */
        getHashAsArray(): Array<string>;

        /**
         * Removes all event listeners, stops hasher and destroy hasher object.
         * - IMPORTANT: hasher won't work after calling this method, hasher Object will be deleted.
         */
        dispose ();

        /**
         * A string representation of the object.
         */
        toString():string;
    }
}

declare var hasher:HasherJs.HasherJsStatic;
