# Predefined image processing styles

WebP and AVIF output requires a predefined style and uses bundled ImageMagick.
No separate ImageMagick installation or runtime download is required. Existing JPEG/PNG/GIF
processing continues to use ImageIO.

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

WebP/AVIF conversion requires a style. All WebP/AVIF image requests, including
original-file pass-through, also require a nonempty, matching path fingerprint.
Missing, stale, or mismatched fingerprints return HTTP 400 before reading the
source binary or starting conversion, including on HEAD requests. Styled
fingerprints cover the source, requested scale, aspect ratio, quality, filter,
and background. Changing dimensions in a styled URL requires a new fingerprint.
This is a content fingerprint, not a secret-key URL signature.

The image service resolves styles again before processing. Content permissions,
stored cropping, focal point, and orientation still apply.

Configure `com.enonic.xp.image.cfg`:

```properties
encoding.enabled = true
encoding.maxConcurrent = 2
encoding.maxQueue = 8
encoding.queueTimeoutSeconds = 5
encoding.timeoutSeconds = 30
encoding.maxPixels = 40000000
```

The build downloads checksum-pinned upstream portable distributions and embeds
both the executable and its codec libraries in the image bundle. On first use,
XP selects the platform resource and extracts it into a private directory under
`java.io.tmpdir`. That directory must allow execution. The extracted distribution
is reused for the lifetime of the bundle and removed on JVM shutdown. Linux
extraction does not require FUSE. This follows the platform-resource/extraction
approach used by native-library loaders such as Brotli4j; conversion runs in an
isolated process so timeouts can terminate native code.

Bundled platforms currently cover Linux x86-64 and Windows x86-64/ARM64 using
ImageMagick 7.1.2-31. Other platforms report an unavailable encoder for modern
output; existing ImageIO processing remains available. The complete upstream
archives retain their licenses and dependencies. Version and SHA-256 pins live
in `native/distributions.json`. Building XP requires 7-Zip to repack upstream
Windows archives as ZIP; production servers do not need it. Install `p7zip-full`
on Linux or 7-Zip on Windows. Use `-PimageMagickSevenZip=/path/to/7z` to select
a build-time extractor (for example Homebrew's `7zz` on macOS).
Adding another platform requires a portable
upstream distribution and a native encoding test on that platform.

No process is started for ordinary formats or modern-format cache hits.
Original WebP/AVIF files pass through only with a matching fingerprint.
SVG pass-through remains unchanged. Styled processing currently accepts formats decoded by
the existing ImageIO backend; WebP/AVIF/SVG inputs are rejected, and GIF styles
process the first frame.

Modern cache misses have bounded concurrency and a bounded waiting queue.
Capacity exhaustion and queue timeout return HTTP 429. Source and output pixel
counts are checked, and the existing heap memory estimate is a hard admission
limit for modern conversions. The external encoder receives only a generated
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
Old JPEG/PNG/GIF fingerprints lose immutable caching after a style change;
WebP/AVIF requests with old fingerprints are rejected. A style change during
request processing is rejected before encoding.

Tests cover style fingerprints and enforcement, cache coalescing, encoder failure
and timeout cleanup, and actual WebP/AVIF output using the embedded distribution.
