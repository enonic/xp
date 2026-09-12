# Predefined image processing styles

WebP and AVIF output requires a predefined style and the `ImageMagic` encoding backend.
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
scale (for example `width-640`), the output extension (`photo.jpg.webp`), and
`?style=com.example.site%3Acard`. Both the legacy image endpoint and `media:image`
support this form. Scale remains required.

When a style defines `aspectRatio`, the request must use `width(px)` or
`height(px)`. XP calculates the other dimension, rounds to the nearest pixel,
and applies a block crop: `16:9` with `width(640)` or `height(360)` becomes
`block(640,360)`. Two-dimensional scales, `max`, and `full` are rejected with
an aspect ratio. Without an aspect ratio, all existing scales remain available.
The same style can be reused at different responsive sizes.

Optional `quality` defaults to 85; optional `filter` uses the existing filter
syntax and limits. `background` is a hexadecimal RGB value used when flattening
to JPEG/GIF; PNG/WebP/AVIF preserve transparency. Style URLs reject quality,
filter, and background overrides, empty query overrides and duplicate styles.
Scale is supplied in the path; a raw `scale` or `format` query override is not
supported. Output format is selected by the extension.

WebP/AVIF conversion requires a style and a valid signed path fingerprint on
cache misses. Styled fingerprints cover the source, requested scale, aspect
ratio, quality, filter, and background, authenticated with HMAC-SHA512 using
XP's existing `generic-hmac-sha512` key. The common HMAC service is also used by
redirect checksums; an image-specific prefix separates the two uses. Existing
redirect checksum values are unchanged. Styled path fingerprints are 40 hex
characters and are compared in constant time.

An existing rendition can be served even if the URL fingerprint is wrong or
stale. A supplied mismatched fingerprint makes the request cache-only for every
output format; missing fingerprints also mean cache-only for WebP/AVIF. A cache
miss returns HTTP 400 without reading source bytes, acquiring encoder capacity,
or creating a cache file. Cache hits work even when modern encoding is disabled.
Unsigned JPEG/PNG/GIF requests retain normal processing. These rules also apply
to HEAD requests. Original-file pass-through needs no regeneration and remains
available without a valid fingerprint.

The disk cache is keyed by the actual source checksum and resolved processing
settings, not by an untrusted URL hash. A stale URL can therefore serve the
currently resolved rendition if it is cached; this does not retrieve historical
renditions by their old URL fingerprint. Mismatched fingerprints do not receive
immutable response caching headers.

The image service resolves styles again before processing. Content permissions,
stored cropping, focal point, and orientation still apply.

Configure `com.enonic.xp.image.cfg`:

```properties
encoding.backend = ImageMagic
encoding.maxConcurrent = 2
encoding.maxQueue = 8
encoding.queueTimeoutSeconds = 5
encoding.timeoutSeconds = 30
encoding.maxPixels = 40000000
```

`encoding.backend` accepts exactly `ImageIO` (default) or `ImageMagic`. Invalid
values fail configuration. `ImageMagic` selects the bundled output encoder for
JPEG, PNG, GIF, WebP, and AVIF. ImageIO still decodes and transforms source images.
With `ImageIO`, new WebP/AVIF conversions are rejected; existing cached ones are
served. JPEG/PNG/GIF cache entries are separate for each backend.

The build downloads checksum-pinned portable distributions and embeds
both the executable and its codec libraries in the image bundle. On first use,
XP selects the platform resource and extracts it into a private directory under
`java.io.tmpdir`. That directory must allow execution. The extracted distribution
is reused for the lifetime of the bundle and removed on JVM shutdown. Linux
extraction does not require FUSE. This follows the platform-resource/extraction
approach used by native-library loaders such as Brotli4j; conversion runs in an
isolated process so timeouts can terminate native code.

Bundled platforms cover Linux x86-64/ARM64, Windows x86-64/ARM64, and macOS ARM64.
Linux ARM64 uses pkgforge's ImageMagick 7.1.2-30 AppImage, with its self-update
hook removed before use. Linux x86-64 and Windows use upstream 7.1.2-31.
macOS ARM64 uses conda-forge 7.1.2-31 with its codec libraries. Other platforms
report an unavailable encoder when `ImageMagic` is selected; `ImageIO` remains available. The complete upstream
archives retain their licenses and dependencies. Version and SHA-256 pins live
in `native/distributions.json` and `native/macos-aarch64.json`.
The macOS packager retains the executable, required library closure, HEIF plugins,
and licenses without installing Conda. Building XP requires Python 3 and 7-Zip to repack upstream
Windows archives as ZIP; production servers do not need it. Install `p7zip-full`
and `zstd` on Linux or 7-Zip 24.01+ on Windows. Use `-PimageMagickSevenZip=/path/to/7z` to select
a build-time extractor (Homebrew's `7zz` is the macOS default).
`-PimageMagickPython=/path/to/python3` selects the build-time Python executable.
Adding another platform requires a portable
upstream distribution and a native encoding test on that platform.

No process is started for cache hits or when encoding with `ImageIO`.
Original WebP/AVIF/SVG files retain their pass-through behavior. Styled processing currently accepts formats decoded by
the existing ImageIO backend; WebP/AVIF/SVG inputs are rejected, and GIF styles
process the first frame.

`ImageMagic` cache misses have bounded concurrency and a bounded waiting queue.
Capacity exhaustion and queue timeout return HTTP 429. Source and output pixel
counts are checked, and the existing heap memory estimate is a hard admission
limit for native conversions. The external encoder receives only a generated
PNG and fixed arguments, uses one configured ImageMagick thread, and is killed
on timeout or interruption. Temporary files and failed cache entries are
removed. ImageMagick pixel-cache memory/map/disk limits are also set; these are
not an operating-system limit on all memory allocated by codec libraries.

The cache key includes the source checksum and resolved processing parameters.
Concurrent requests recheck the cache after obtaining the file lock, preventing
duplicate conversions. The URL fingerprint includes the requested scale and the
style's aspect ratio, quality, filter, and background. Different output formats
have distinct URL extensions and disk cache keys. The handler also checks the
fingerprint before applying the configured public/private immutable cache header.
Old fingerprints lose immutable caching after a style change and allow only
existing cache entries to be served. Cache misses cannot regenerate the image. A style change during
request processing is rejected before encoding.

Tests cover HMAC fingerprints, redirect compatibility, cache-only hits/misses,
style enforcement, cache coalescing, encoder failure
and timeout cleanup, and actual WebP/AVIF output using the embedded distribution.
