/**
 * Functions to pass java-types in JSON, typically usage is to type e.g a Geo-point value when creating nodes in node-lib.
 *
 * @example
 * var valueLib = require('/lib/xp/value');
 *
 * @module value
 */

declare global {
    interface XpLibraries {
        '/lib/xp/value': typeof import('./value');
    }
}

import type {ByteSource} from '@enonic-types/core';

export type {ByteSource} from '@enonic-types/core';

function pad(value: number): string {
    if (value < 10) {
        return `0${value}`;
    }
    return String(value);
}

function toLocalDateString(date: Date): string {
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

function toLocalTimeString(date: Date): string {
    return date.toTimeString().substring(0, 8);
}

function toLocalDateTimeString(date: Date): string {
    return `${toLocalDateString(date)}T${toLocalTimeString(date)}`;
}

export interface GeoPoint {
    getLatitude(): number;

    getLongitude(): number;

    toString(): string;
}

interface GeoPointHandler {
    newInstance(latitude: number, longitude: number): GeoPoint;

    from(value: string): GeoPoint;
}

/**
 * Creates a GeoPoint java-type.
 * @param {number} lat Latitude
 * @param {number} lon Longitude
 *
 * @returns {*} GeoPoint java-type
 */
export function geoPoint(lat: number, lon: number): GeoPoint {
    const bean: GeoPointHandler = __.newBean<GeoPointHandler>('com.enonic.xp.lib.value.GeoPointHandler');
    return bean.newInstance(lat, lon);
}

/**
 * Creates a GeoPoint java-type.
 * @param {string} value comma-separated lat and lon
 *
 * @returns {*} GeoPoint java-type
 */
export function geoPointString(value: string): GeoPoint {
    const bean: GeoPointHandler = __.newBean<GeoPointHandler>('com.enonic.xp.lib.value.GeoPointHandler');
    return bean.from(value);
}


export interface Instant {
    getEpochSecond(): number;

    getNano(): number;

    toEpochMilli(): number;
}

interface InstantHandler {
    parse(value: string): Instant;
}

/**
 * Creates a Instant java-type.
 * @param {string|Date} value An ISO-8601-formatted instant (e.g '2011-12-03T10:15:30Z'), or a Date object.
 *
 * @returns {*} Instant java-type
 */
export function instant(value: string | Date): Instant {
    const bean: InstantHandler = __.newBean<InstantHandler>('com.enonic.xp.lib.value.InstantHandler');

    if (typeof value === 'string') {
        return bean.parse(value);
    } else {
        return bean.parse(value.toISOString());
    }
}

export interface Reference {
    toString(): string;

    getNodeId(): string;
}

interface ReferenceHandler {
    from(value: string): Reference;
}

/**
 * Creates a Reference java-type.
 * @param {string} value A nodeId as string (e.g '1234-5678-91011')
 *
 * @returns {*} Reference java-type
 */
export function reference(value: string): Reference {
    const refBean: ReferenceHandler = __.newBean<ReferenceHandler>('com.enonic.xp.lib.value.ReferenceHandler');
    return refBean.from(value);
}

export type Month =
    | 'JANUARY'
    | 'FEBRUARY'
    | 'MARCH'
    | 'APRIL'
    | 'MAY'
    | 'JUNE'
    | 'JULY'
    | 'AUGUST'
    | 'SEPTEMBER'
    | 'OCTOBER'
    | 'NOVEMBER'
    | 'DECEMBER';

export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

export interface LocalDateTime {
    getYear(): number;

    getMonthValue(): number;

    getMonth(): Month;

    getDayOfMonth(): number;

    getDayOfWeek(): DayOfWeek;

    getHour(): number;

    getMinute(): number;

    getSecond(): number;

    getNano(): number;
}

interface LocalDateTimeHandler {
    parse(value: string): LocalDateTime;
}

/**
 * Creates a LocalDateTime java-type.
 * @param {string|Date} value A local date-time string (e.g '2007-12-03T10:15:30'), or a Date object.
 *
 * @returns {*} LocalDateTime java-type
 */
export function localDateTime(value: string | Date): LocalDateTime {
    const bean: LocalDateTimeHandler = __.newBean<LocalDateTimeHandler>('com.enonic.xp.lib.value.LocalDateTimeHandler');
    if (typeof value === 'string') {
        return bean.parse(value);
    } else {
        return bean.parse(toLocalDateTimeString(value));
    }
}

export interface LocalDate {
    getYear(): number;

    getMonthValue(): number;

    getMonth(): Month;

    getDayOfMonth(): number;

    getDayOfYear(): number;

    getDayOfWeek(): DayOfWeek;

    isLeapYear(): boolean;
}

interface LocalDateHandler {
    parse(value: string): LocalDate;
}

/**
 * Creates a LocalDate java-type.
 * @param {string|Date} value A ISO local date-time string (e.g '2011-12-03'), or a Date object.
 *
 * @returns {*} LocalDate java-type
 */
export function localDate(value: string | Date): LocalDate {
    const bean: LocalDateHandler = __.newBean<LocalDateHandler>('com.enonic.xp.lib.value.LocalDateHandler');

    if (typeof value === 'string') {
        return bean.parse(value);
    } else {
        return bean.parse(toLocalDateString(value));
    }
}

export interface LocalTime {
    getHour(): number;

    getMinute(): number;

    getSecond(): number;

    getNano(): number;
}

interface LocalTimeHandler {
    parse(value: string): LocalTime;
}

/**
 * Creates a LocalTime java-type.
 * @param {string|Date} value A ISO local date-time string (e.g '10:15:30'), or a Date object.
 *
 * @returns {*} LocalTime java-type
 */
export function localTime(value: string | Date): LocalTime {
    const bean: LocalTimeHandler = __.newBean<LocalTimeHandler>('com.enonic.xp.lib.value.LocalTimeHandler');
    if (typeof value === 'string') {
        return bean.parse(value);
    } else {
        return bean.parse(toLocalTimeString(value));
    }
}

export interface BinaryReference {
    toString(): string;
}

interface BinaryReferenceHandler {
    from(value: string): BinaryReference;
}

export interface BinaryAttachment {
    getReference(): BinaryReference;

    getByteSource(): ByteSource;
}

interface BinaryAttachmentHandler {
    newInstance(ref: BinaryReference, byteSource: ByteSource): BinaryAttachment;
}

/**
 * Creates a BinaryAttachment java-type.
 * @param {string} name The binary name
 * @param stream The binary stream
 *
 * @returns {*} BinaryAttachment java-type
 */
export function binary(name: string, stream: ByteSource): BinaryAttachment {
    const binaryReferenceBean: BinaryReferenceHandler = __.newBean<BinaryReferenceHandler>('com.enonic.xp.lib.value.BinaryReferenceHandler');
    const binaryAttachmentBean: BinaryAttachmentHandler = __.newBean<BinaryAttachmentHandler>('com.enonic.xp.lib.value.BinaryAttachmentHandler');

    return binaryAttachmentBean.newInstance(binaryReferenceBean.from(name), stream);
}
