# Predefined image processing styles

WebP and AVIF output requires the `ImageMagic` encoding backend and a matching path
fingerprint; a predefined style is optional. Unstyled WebP/AVIF URLs take quality,
filter and background from the request, like any other unstyled output format.
No separate ImageMagick installation or runtime download is required. The default
`ImageIO` backend preserves existing JPEG/PNG/GIF encoding.

Define the processing style in the application's `cms/style/style.yml`:

```yaml
kind: Style
styles:
  - type: Image
    name: card
    label: Card
    aspectRatio: "16:9"
    quality: 80
```

Generate the URL with:

```javascript
portal.imageUrl({ id: imageId, style: 'com.example.site:card', scale: 'width(640)', format: 'webp' });
portal.imageUrl({ id: imageId, style: 'com.example.site:card', scale: 'width(640)', format: 'avif' });
```

The style contains neither scale nor output format. Supply both using the existing
`scale` and `format` arguments to `portal.imageUrl`. The URL contains the requested
scale and style in the same path segment (`width-640~com.example.site:card`),
followed by the filename and output extension (`photo.jpg.webp`). The first `~`
separates scale from the fully qualified style alias. Unstyled URLs retain their
existing scale segment. A `style` query parameter is rejected. Both the legacy image endpoint and `media:image`
support this form. Scale remains required.

Rich text stores new processing references as
`image://<content-id>?style=<application>:<style>`, without copying scale, filters,
quality, background or format into the saved link. `portal.processHtml` generates
signed image URLs from that reference, including separate signatures for responsive
widths. Editing the style changes generated fingerprints without rewriting content.
Legacy links with raw parameters or unqualified style names retain their existing
rendering behavior. Qualified references cannot be combined with raw parameters.

A missing style in an image URL returns HTTP 404 for GET and HEAD, before source
or cache access. There is no fallback to an unstyled image. Malformed references
and conflicting parameters remain HTTP 400. Rich-text rendering of a missing
qualified style produces the existing 404 error URL.

The style's `aspectRatio` is a hint for `width(px)` and `height(px)`.
XP calculates the other dimension, rounds to the nearest pixel,
and applies a block crop: `16:9` with `width(640)` or `height(360)` becomes
`block(640,360)`. Other scale modes retain their own geometry: explicit
`block(640,480)`, `square(640)`, `max(640)`, and `full()` remain valid.
The URL always retains the requested scale; applying the hint changes only
processing geometry. The style settings still participate in the fingerprint.
The same style can be reused at different responsive sizes.

Optional `quality` defaults to 85; optional `filter` uses the existing filter
syntax and limits. `background` is a hexadecimal RGB value used when flattening
to JPEG/GIF; PNG/WebP/AVIF preserve transparency. Style URLs reject quality,
filter, and background overrides, including empty query overrides.
Scale is supplied in the path; a raw `scale` or `format` query override is not
supported. Output format is selected by the extension.

Every styled conversion requires a matching modern path fingerprint on cache
misses, for every output format. WebP/AVIF always requires one, styled or not. New
image URLs, including unstyled URLs and rich-text renditions, use modern
fingerprints covering the source, requested scale, output MIME type, aspect
ratio, quality, filter, and background, authenticated with HMAC-SHA512 using
a named key derived from XP's `generic-hmac-sha512` secret through HKDF-SHA512.
Images use `image-fingerprint-v3`; redirects use the separate `redirect-checksum-v1`
key. Redirect tickets issued with the previous key must be regenerated. Modern path fingerprints are 40 hex
characters and are compared in constant time.

An existing rendition can be served even if the URL fingerprint is wrong or
stale. All legacy fingerprints (even matching source hashes) and mismatched modern
fingerprints make the request cache-only for every output format. Missing
fingerprints also mean cache-only for every styled request. A cache
miss returns HTTP 400 without reading source bytes, acquiring encoder capacity,
or creating a cache file. Cache hits work even when modern encoding is disabled.
Hashless regeneration is disabled by default. To restore unsigned, unstyled
JPEG/PNG/GIF processing for compatibility, set
`image.allowHashlessGeneration = true` in `com.enonic.xp.portal.cfg`. This option
applies to both image endpoints and can be changed at runtime. It never permits
unsigned styled generation, WebP/AVIF conversion without a matching signature,
or generation with a supplied legacy or mismatched hash. The option covers no
WebP/AVIF request: those formats are generated only for a signature XP issued.
With the default configuration, removing both style and hash still permits only
cache reads.
Changing output format invalidates a modern signature; format remains outside
the style definition. These rules also apply
to HEAD requests. Original-file pass-through needs no regeneration and remains
available without a valid fingerprint. A URL passes through only when it requests
neither scaling nor format conversion: a scale other than `full()` is processed
like any other request, so the returned bytes always match what the URL asks for.

Immutable `Cache-Control` is sent only for a request whose query parameters the
endpoint acts on: `filter`, `quality` and `background` for images, `download` for
attachments. Any other parameter leaves the response unchanged but adds a shared-cache
key, so those responses fall back to the private header and shared caches do not store
them. Tampering with a signed parameter already invalidates the fingerprint instead.

The disk cache is keyed by the actual source checksum and resolved processing
settings, not by an untrusted URL hash. A stale URL can therefore serve the
currently resolved rendition if it is cached; this does not retrieve historical
renditions by their old URL fingerprint. Mismatched fingerprints do not receive
immutable response caching headers.

The portal resolves image styles through `StyleDescriptorService.getImageStyle(DescriptorKey)`.
`ImageService` has no style lookup or style descriptor service dependency.
The portal passes the resolved style snapshot used to validate the fingerprint to
the image service. Content permissions,
stored cropping, focal point, and orientation still apply.

Configure `com.enonic.xp.image.cfg`:

```properties
decoding.backend = ImageMagic
decoding.maxBytes = 256mb
transformation.backend = ImageMagic
encoding.backend = ImageMagic

# Shared across native decoding, transformations and encoding.
processing.maxConcurrent = 2
processing.maxQueue = 8
processing.queueTimeoutSeconds = 5
processing.timeoutSeconds = 30
processing.maxPixels = 40000000
processing.maxDisk = 4gb
```

`encoding.backend` accepts exactly `ImageIO` (default) or `ImageMagic`. Invalid
values fail configuration. `ImageMagic` selects the bundled output encoder for
JPEG, PNG, GIF, WebP, and AVIF.
With `ImageIO`, new WebP/AVIF conversions are rejected; existing cached ones are
served. JPEG/PNG/GIF cache entries are separate for each encoding backend.

`decoding.backend` independently accepts `ImageIO` (default) or `ImageMagic`.
The native decoder supports JPEG, PNG, BMP, TIFF, WebP, and AVIF inputs.
GIF and SVG inputs are rejected before starting a native process. Existing ImageIO
GIF decoding is preserved. The native decoder reads the first frame into an 8-bit RGBA image; XP then applies
the transformation backend selected below. The decoding setting does not select
a transformation implementation. For example, native
WebP/AVIF decoding can be combined with ImageIO JPEG/PNG output. Native
output encoding can also be combined with ImageIO input decoding. Switching the
decoder uses separate disk cache entries; existing ImageIO cache keys are unchanged.

`transformation.backend` independently accepts `ImageIO` (default) or `ImageMagic`.
`ImageIO` retains XP's existing Java transformations. `ImageMagic` runs orientation,
stored cropping, scaling, ordered filters, and background flattening in the bundled
native executable. All three backend settings can be mixed. Adjacent Java stages pass
the BufferedImage directly; adjacent native stages pass raw RGBA files directly.
Java/native boundaries transfer 8-bit sRGB pixels with straight alpha in small,
bounded buffers, without intermediate PNG compression or decompression. Java color
models, channel layouts, palettes and premultiplied alpha are normalized during transfer.
Dimensions and alpha presence are carried separately, and each raw file must contain
exactly width × height × 4 bytes before consumption. A 1024 × 768 intermediate is
3 MiB; a 40-million-pixel intermediate is about 153 MiB. Files remain temporary
and are deleted when their owning stage closes.
Selecting native transformations alone does not enable native
source formats or WebP/AVIF output encoding.

Native scaling reuses XP's dimension calculations, including aspect-ratio styles,
focal points, and crop-relative focal-point adjustment. Orientation is applied
before the stored crop; scale follows the crop, filters follow scaling, and JPEG/GIF
background flattening is last. Intermediate resize dimensions are checked before
processing, even if the final crop is small. Native transformations have separate
cache entries and share the native concurrency queue and heap admission limits.
Cache-only requests still never regenerate a missing rendition.

All existing filter names are supported: block, blur, border, bump, colorize, edge,
emboss, fliph, flipv, rotate90/180/270, gamma, grayscale, hsbadjust, hsbcolorize, invert,
rgbadjust, rounded, sepia, and sharpen. Existing filter count and argument limits
apply; non-finite numeric arguments are rejected. Commands and expressions are
constructed from fixed operators and parsed numbers, with no caller-supplied paths
or ImageMagick programs. The transformation policy permits only raw RGBA I/O and disables
external delegates and loadable filters.

Pixel output is not guaranteed to match the Java backend: native resizing uses
Lanczos, and blur, edge, emboss, block sampling, convolution, and antialiasing use
ImageMagick algorithms. Geometric dimensions and operation order remain the same.
For compatibility, `flipv` retains the current Java implementation's horizontal-flip
behavior; EXIF vertical mirroring uses a vertical flip. Use `ImageIO` where exact
legacy rendering is required.

Requests using any native stage bound source reads by `decoding.maxBytes` (`256mb`, or 256 MiB, by default).
It accepts XP's size syntax, such as `512mb`, `1gb`, or a plain byte count.
Suffixes are case-insensitive and use powers of 1024.
The source is copied and hashed once, within request admission, and that verified
file is used for decoding. Missing checksum metadata never triggers an unbounded
read before admission. Source copying and hashing are storage I/O; the native
stage timeout starts when a native process is invoked.
Native policy disables GIF, SVG and vector rendering coders, external delegates,
loadable filters, indirect file reads, and unrelated coders. SVG output and
compressed SVGZ input are not supported.

The separate `core-image-im` OSGi bundle owns the native distributions, published
by `im4j` as ordinary executable and library resources rather than nested AppImage
or ZIP archives. Nothing is unpacked on the server, and no target-platform
executables run during XP's build.

On first native use, a Declarative Services component copies the selected
platform's resources to a unique installation directory in its bundle data area
(under XP's OSGi storage). That filesystem must allow execution. Executable
permissions are restored from the build-generated file index. The installation
is reused by that component. Bundle deactivation rejects new installation handles;
existing handles keep the files until their processes finish, after which cleanup
removes the installation. Bundle restart creates a fresh installation. There is no
static executable cache or JVM shutdown hook. A crash can leave an old installation
in the framework data area; XP's configured OSGi storage cleanup removes it on restart.

`core-image` retains the image pipeline and obtains installations through the
`com.enonic.im4j.ImageMagick` service contract, provided by the `im4j` bundle.
No implementation package is shared between `core-image` and `core-image-im`.
Conversion runs in a separate process so timeouts can terminate native code.

Bundled platforms cover Linux x86-64/ARM64, Windows x86-64/ARM64, and macOS ARM64.
Linux ARM64 uses pkgforge's ImageMagick 7.1.2-30 AppImage, with its self-update
hook removed during packaging. Linux x86-64 and Windows use upstream 7.1.2-31.
Windows distributions use Q16-HDRI so edge and emboss retain sufficient precision
in intermediate gradients; the raw transfer and final output still use 8-bit channels.
macOS ARM64 uses conda-forge 7.1.2-31 with its codec libraries. Other platforms
report an unavailable native backend when `ImageMagic` is selected; `ImageIO` remains available. The complete upstream
archives retain their licenses and dependencies, and the packaging logic and
version/SHA-256 pins that produce each artifact now live in the `im4j` project.

Building XP requires no ImageMagick tooling. The native distributions are packaged
by the `im4j` project, which publishes one checksum-pinned artifact per platform;
XP depends on them and `xp-distro` selects the one matching its target. Adding a
platform is an im4j change: a portable upstream distribution, a manifest entry, and
a native encoding test on that platform.

No process is started for cache hits or when all three backends use `ImageIO`.
Unstyled WebP/AVIF originals retain their pass-through behavior when the URL requests
no scaling or conversion; a requested scale is applied like any other raster source.
GIF and SVG have no raster rendition without a style, so `portal.imageUrl` addresses
those sources through the attachment endpoint rather than signing a scale the image
endpoint would ignore, and rich text emits a single `src` for them with no `srcset`.
Already published image URLs for GIF and SVG keep passing through unchanged.
Styled processing uses the configured decoder. GIF styles require ImageIO decoding
and process the first frame.

Native decoding, transformations, and encoding share the `processing.*` concurrency, queue, timeout,
and pixel limits. The timeout bounds each native stage. A request holds one capacity slot across decoding, transformations,
and encoding. The native decoder probes dimensions before Java raster allocation;
probe and decode share one timeout budget. `ImageMagic` cache misses have bounded
concurrency and a bounded waiting queue.
Requests for the same rendition share the leader's result, including failures and
native stage timeouts. The queue timeout only bounds the leader's wait for a
processing slot; followers await that shared result and remain interruptible.
Followers count against request capacity but do not hold processing slots. Cache locks use exact keys,
so unrelated renditions cannot block each other through lock striping.
Capacity exhaustion and queue timeout return HTTP 429. Source and output pixel
counts are checked, and the existing heap memory estimate is a hard admission
limit for native conversions. Both transformation backends calculate orientation,
crop and intermediate resize dimensions before allocation and use that geometry
for execution. The external encoder receives only a generated
raw RGBA raster and fixed arguments, uses one configured ImageMagick thread, and is killed
on timeout or interruption. Temporary files and failed cache entries are
removed. ImageMagick retains at most 256 MiB of pixel cache in memory and disables
memory mapping. Larger pixel caches spill to the stage's private temporary directory
under `work/cache/img/decoding`, `transformation` or `encoding`. `processing.maxDisk`
limits this spill per native process (default `4gb`; `0` disables spill), so concurrent
processes can each consume that budget. It excludes source copies, raw handoff files
and final rendition files. Spill files are removed when the stage closes, including
failure, interruption and timeout. These limits do not bound all memory allocated
by codec libraries; source/pixel limits and heap admission still apply.

Native decoding converts embedded ICC profiles to sRGB before discarding metadata.
ImageIO does not apply EXIF orientation, and the native pipeline does not invoke
`auto-orient`. Upload processing stores EXIF orientation in `media.orientation`. The transformer applies
that stored rotation/flip once, then the stored crop in oriented coordinates, then
scaling with the focal point remapped into the crop. Stored orientation edits take
precedence over the source EXIF tag. AVIF/HEIF container transforms are distinct from
EXIF orientation and remain part of decoding the container's image geometry.
Native decoder cache version 2 separates corrected colour handling from earlier renditions.
Native transformation cache version 5 also invalidates mirrored-rotation crops made
with stale ImageMagick virtual-canvas offsets; transpose/transverse reset that canvas
before the stored crop is applied.

The cache key includes the source checksum and resolved processing parameters.
Concurrent requests recheck the cache after obtaining the file lock, preventing
duplicate conversions. The URL fingerprint includes the requested scale and the
style's aspect ratio, quality, filter, background, and the requested output MIME
type. Different output formats have distinct signatures and disk cache keys. The handler also checks the
fingerprint before applying the configured public/private immutable cache header.
Old fingerprints lose immutable caching after a style change and allow only
existing cache entries to be served. Cache misses cannot regenerate the image. Each request keeps its resolved style snapshot through processing.
Style defaults and validation share an immutable processing specification with
fingerprint generation. Both the core image API and URL API reject explicit
quality, filter or background overrides when a style is selected.

Tests cover HMAC fingerprints, redirect compatibility, cache-only hits/misses,
style enforcement, cache coalescing, encoder/decoder failure and timeout cleanup,
independent backend selection, decoder cache separation, native GIF/SVG rejection,
preserved ImageIO GIF decoding, native geometry/orientation/filter operations, mixed
backend combinations, actual PNG/JPEG/WebP/AVIF decoding, and WebP/AVIF output
using the embedded distribution.
