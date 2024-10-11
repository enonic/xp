import type {
    HttpFilterController,
    HttpFilterNext,
    Request,
    RequestHandler,
    Response,
    SerializableRequest,
} from '../core/index';

import {
    expectAssignable,
    // expectDeprecated,
    // expectDocCommentIncludes,
    // expectError,
    // expectNever,
	// expectNotAssignable,
    // printType,
    // expectNotDeprecated,
    // expectNotType,
    // expectType,
    // printType,
} from 'tsd';

const log = {
    info: (message?: string, ...optionalParams: unknown[]) => { /* no-op */ },
};

// Testing examples from HTTP Filters
// https://developer.enonic.com/docs/xp/stable/framework/filters


// ────────────────────────────────────────────────────────────────────────────
// Minimal filter timing the subsequent request
// ────────────────────────────────────────────────────────────────────────────
const httpFilterController1 = {
    filter: function (req: Request, next: RequestHandler) {
        const before = new Date().getTime();
        const response = next(req);  // next(req) hands over the request to the engine pipeline and returns the response
        const after = new Date().getTime();
        log.info('%sms', after - before);
        return response;
    },
};
expectAssignable<HttpFilterController>(httpFilterController1);

// ────────────────────────────────────────────────────────────────────────────
// Filter manipulating the request and the response
// ────────────────────────────────────────────────────────────────────────────
const httpFilterController2 = {
    filter: function (req: Request, next: HttpFilterNext<SerializableRequest<Request>, Response>) {
        // ERROR: I don't think one can add custom properties to the request, only headers...
        // req.requestLogging = true; // Manipulate request
        req.cookies = {
            ...req.cookies,
            'X-Auth-Token': 'letMeIn',
        };
        log.info('Request:%s', JSON.stringify(req, null, 2));
        const response = next(req); // Continue request pipeline
        // response.responseLogging = true; // Manipulate response
        if (!response.headers) {
            response.headers = {};
        }
        response.headers['X-Response-Logging'] = 'true';
        log.info('Response:%s', JSON.stringify(response, null, 2));
        return response as unknown as Response;
    },
};
expectAssignable<HttpFilterController<Request, Response, Response>>(httpFilterController2);

// ────────────────────────────────────────────────────────────────────────────
// Filter intercepting the request
// ────────────────────────────────────────────────────────────────────────────
const httpFilterController3 = {
    filter: function (req: Request, next: RequestHandler) {
        if (req.getHeader('X-Auth-Token') !== 'letMeIn') {
            // intercept request pipeline
            return {
                status: 403,
            } as Response;
        }
    
        // req.headers['Authenticated'] = true; // ERROR: Wrong type in example
        req.headers['Authenticated'] = 'true';
        return next(req);
    },
};
expectAssignable<HttpFilterController>(httpFilterController3);

// ────────────────────────────────────────────────────────────────────────────
// Filter changing request params
// ────────────────────────────────────────────────────────────────────────────
const httpFilterController4 = {
    filter: function (req: Request, next: RequestHandler) {
        req.params = {
            param1: 'val', // if param1 was not in the original request it will be added, otherwise the original value will be replaced
            // param2: null, // remove param2 from the original request // ERROR: Wrong type in example
            // param2: undefined, // remove param2 from the original request // ERROR: This is not allowed either
            param3: [], // another way to remove a parameter
        };
        return next(req);
    },
};
expectAssignable<HttpFilterController>(httpFilterController4);