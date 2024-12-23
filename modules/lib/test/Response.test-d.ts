import type {
    LiteralUnion,
    Response,
    ResponseBody,
} from '../core/index';

import {
    expectAssignable,
	expectNotAssignable,
} from 'tsd';


// Scenario: Empty OK response
const emptyOkResponse = {};
expectAssignable<Response>(emptyOkResponse);


// Scenario: Response with just status
const notFoundResponse  = {
    status: 404,
};
expectAssignable<Response>(notFoundResponse);

// ────────────────────────────────────────────────────────────────────────────
// Scenario: Strictify Response down to a specific Response
// ────────────────────────────────────────────────────────────────────────────
type NotFoundResponse = Response<{
    status: 404
}>;

expectAssignable<NotFoundResponse>({
    status: 404 as const,
});

expectNotAssignable<NotFoundResponse>({
    status: 200,
});

// ────────────────────────────────────────────────────────────────────────────
// Scenario: JSON body
// ────────────────────────────────────────────────────────────────────────────
type MyObject = {
    key: string
};
const object = {
    key: 'value',
};

type JsonResponse<Body extends ResponseBody> = Response<{
    body: Body,
    contentType: LiteralUnion<'application/json'>
}>;

const jsonResponse = {
    body: object,
    // Changing contentType is allowed since it's a LiteralUnion
    contentType: 'application/json;charset=utf-8',
};

// printType(jsonResponse);

// Untyped body object allowed
expectAssignable<Response>(jsonResponse);

// Typed body object also allowed
expectAssignable<JsonResponse<MyObject>>(jsonResponse);


expectAssignable<Response>({
    body: undefined,
});

// expectAssignable<Response>({
//     body: null,
// });

expectAssignable<Response>({
    cookies: {}, // Empty cookies object
});

expectAssignable<Response>({
    headers: {}, // Empty headers object
});

// Scenario: Response with "everything"
const fullResponse = {
    applyFilters: true,
    body: 'Hello, world!',
    contentType: 'text/plain',
    cookies: {
        simpleCookie: 'string',
        complexCookie: {
            value: 'string',
            path: 'string',
            domain: 'string',
            comment: 'string',
            maxAge: 123,
            secure: true,
            httpOnly: true,
            sameSite: 'string',
        },
    },
    headers: {
        'Cache-Control': 'http1',
        'content-encoding': 'http2',
        Etag: 123,
        'X-My-Header': 'my-value',      
    },
    postProcess: true,
    redirect: '/some/other/url',
    status: 200,
};
expectAssignable<Response>(fullResponse);

// Check illegal values
expectNotAssignable<Response>({
    applyFilters: 'notBoolean',
});

expectNotAssignable<Response>({
    contentType: true, // Not string!
});

expectNotAssignable<Response>({
    cookies: true, // Not Record<string,string|ComplexCookie>!
});

expectNotAssignable<Response>({
    cookies: {
        'string': 123, // Not string|ComplexCookie !
    },
});

expectNotAssignable<Response>({
    postProcess: 'notBoolean',
});

expectNotAssignable<Response>({
    redirect: true, // Not string!
});

expectNotAssignable<Response>({
    status: '200', // Not number!
});
