/**
 * IO related functions.
 *
 * @example
 * var ioLib = require('/lib/xp/io');
 *
 * @module io
 */

declare global {
    interface XpLibraries {
        '/lib/xp/io': typeof import('./io');
    }
}

import type {Resource} from '@enonic-types/core';

export type {Resource} from '@enonic-types/core';

interface JavaResource {
    getSize(): number;

    getTimestamp(): number;

    getBytes(): object;

    exists(): boolean;
}

interface IOHandlerBean {
    readText(value: object): string;

    readLines(value: object): string[];

    processLines(stream: object, func: (value: string) => object): void;

    getSize(stream: object): number;

    getMimeType(name: string): string;

    newStream(text: string): object;

    getResource(key: string): JavaResource;
}

const bean = __.newBean<IOHandlerBean>('com.enonic.xp.lib.io.IOHandlerBean');

/**
 * Looks up a resource.
 *
 * @constructor
 * @hideconstructor
 * @alias Resource
 */
class ResourceImpl
    implements Resource {
    private res: JavaResource;

    constructor(key: string) {
        this.res = bean.getResource(key);
    }

    /**
     * Returns the resource size.
     *
     * @returns {number} Size of resource in bytes.
     */
    getSize(): number {
        return this.res.getSize();
    }

    /**
     * Returns the resource timestamp.
     *
     * @returns {number} Timestamp of resource creation in milliseconds.
     */
    getTimestamp(): number {
        return this.res.getTimestamp();
    }

    /**
     * Returns the resource stream.
     *
     * @returns Stream of resource.
     */
    getStream(): object {
        return this.res.getBytes();
    }

    /**
     * Returns true if the resource exists.
     *
     * @returns {boolean} True if resource exists.
     */
    exists(): boolean {
        return this.res.exists();
    }
}

/**
 * Read text from a stream.
 *
 * @example-ref examples/io/readText.js
 *
 * @param stream Stream to read text from.
 * @returns {string} Returns the text read from stream or string.
 */
export function readText(stream: object): string {
    return bean.readText(stream);
}

/**
 * Read lines from a stream.
 *
 * @example-ref examples/io/readLines.js
 *
 * @param stream Stream to read lines from.
 * @returns {string[]} Returns lines as an array.
 */
export function readLines(stream: object): string[] {
    return __.toNativeObject(bean.readLines(stream));
}

/**
 * Process lines from a stream.
 *
 * @example-ref examples/io/processLines.js
 *
 * @param stream Stream to read lines from.
 * @param {function} func Callback function to be called for each line.
 */
export function processLines(stream: object, func: (value: string) => object): void {
    return bean.processLines(stream, func);
}

/**
 * Returns the size of a stream.
 *
 * @example-ref examples/io/getSize.js
 *
 * @param stream Stream to get size of.
 * @returns {number} Returns the size of a stream.
 */
export function getSize(stream: object): number {
    return bean.getSize(stream);
}

/**
 * Returns the mime-type from a name or extension.
 *
 * @example-ref examples/io/getMimeType.js
 *
 * @param {string} name Name of file or extension.
 * @returns {string} Mime-type of name or extension.
 */
export function getMimeType(name: string): string {
    return bean.getMimeType(name);
}

/**
 * Returns a new stream from a string.
 *
 * @example-ref examples/io/newStream.js
 *
 * @param {string} text String to create a stream of.
 * @returns {*} A new stream.
 */
export function newStream(text: string): object {
    return bean.newStream(text);
}

/**
 * Looks up a resource.
 *
 * @example-ref examples/io/getResource.js
 *
 * @param {string} key Resource key to look up.
 * @returns {Resource} Resource reference.
 */
export function getResource(key: string): Resource {
    return new ResourceImpl(key);
}
