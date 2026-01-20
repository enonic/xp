import type {
    Request,
    RequestConstructorParams,
    RequestCookies,
    RequestHeaders,
} from '../core/index';

type ReplacerFn = (this: unknown, key: string, value: unknown) => unknown;
type Replacer =  ReplacerFn | undefined;

export function toStr(
	value: unknown,
	replacer? :Replacer,
	space: string | number | undefined = 4,
): string {
	return JSON.stringify(value, replacer, space);
}

const isObject = (value: object | unknown): value is object =>
	Object.prototype.toString.call(value).slice(8,-1) === 'Object';

function mapKeys<T extends Record<string, unknown>>(
	obj: T,
	fn: ({
		key,
		result,
		value,
	}: {
		key: PropertyKey
		result: T
		value: unknown
	}) => void,
): T {
	if (!isObject(obj)) {
		throw new TypeError(`mapKeys: First param must be an object! got:${toStr(obj)}`);
	}
	const result = {} as T;
	const keys = Object.keys(obj);
	for (const key of keys) {
		fn({
			key,
			result,
			value: obj[key],
		});
	}
	return result;
}

function lcKeys<T extends Record<string,unknown>>(obj: T): T {
	return mapKeys(obj,({
		key,
		result,
		value,
	}) => {
        (result as Record<string, unknown>)[String(key).toLowerCase()] = value;
	});
}

export class RequestImplementation implements Request {
    cookies: RequestCookies;
    headers: RequestHeaders;
    host: string;
    method: string;
    mode: string;
    params: Record<string, string | string[]>;
    path: string;
    port: number;
    rawPath: string;
    remoteAddress: string;
    scheme: string;
    url: string;
    webSocket: boolean;

    branch?: string;
    contextPath?: string;
    contentType?: string;
    repositoryId?: string;
    validTicket?: boolean;
    body?: string;

    constructor({
        // Required
        cookies,
        headers,
        host,
        method,
        mode,
        params,
        path,
        port,
        rawPath,
        remoteAddress,
        scheme,
        url,
        webSocket,
        // Optionals
        branch,
        contextPath,
        contentType,
        repositoryId,
        validTicket,
        // Special
        body,
    }: RequestConstructorParams) {
        this.cookies = cookies;
        this.headers = lcKeys(headers);
        this.host = host;
        this.method = method;
        this.mode = mode;
        this.params = params;
        this.path = path;
        this.port = port;
        this.rawPath = rawPath;
        this.remoteAddress = remoteAddress;
        this.scheme = scheme;
        this.url = url;
        this.webSocket = webSocket;

        this.branch = branch;
        this.contextPath = contextPath;
        this.contentType = contentType;
        this.repositoryId = repositoryId;
        this.validTicket = validTicket;

        this.body = body;
    }

    getHeader(header: string): string | null {
        const value = this.headers[header];
        if (value === undefined) {
            return null;
        }
        return value;
    }

    toString(): string {
        return JSON.stringify(this);
    }

    toObject(): Request {
        const obj: Request = {
            cookies: this.cookies,
            headers: this.headers,
            getHeader: (header: string): string | null => {
                const value = obj.headers[header];
                if (value === undefined) {
                    return null;
                }
                return value;
            },
            host: this.host,
            method: this.method,
            mode: this.mode,
            params: this.params,
            path: this.path,
            port: this.port,
            rawPath: this.rawPath,
            remoteAddress: this.remoteAddress,
            scheme: this.scheme,
            url: this.url,
            webSocket: this.webSocket,
        };
        if (this.branch) {
            obj.branch = this.branch;
        }
        if (this.contextPath) {
            obj.contextPath = this.contextPath;
        }
        if (this.contentType) {
            obj.contentType = this.contentType;
            if (!this.body) {
                throw new Error('body is requred when contentType is set');
            }
            obj.body = this.body;
        }
        if (this.repositoryId) {
            obj.repositoryId = this.repositoryId;
        }
        if (this.validTicket) {
            obj.validTicket = this.validTicket;
        }
        return obj;
    }
}
