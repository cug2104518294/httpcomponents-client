package org.apache.hc.client5.http.fluent;

import org.apache.hc.core5.http.ContentType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * This class represents arbitrary content of a specific type that can be consumed
 * multiple times and requires no explicit deallocation used by the fluent facade.
 * <p>
 * 此类表示可以多次使用的特定类型的任意内容，并且不需要流畅外观使用的显式释放。
 *
 * @since 4.2
 */
public class Content {

    public static final Content NO_CONTENT = new Content(new byte[]{}, ContentType.DEFAULT_BINARY);

    private final byte[] raw;
    private final ContentType type;

    public Content(final byte[] raw, final ContentType type) {
        super();
        this.raw = raw;
        this.type = type;
    }

    public ContentType getType() {
        return this.type;
    }

    public byte[] asBytes() {
        return this.raw.clone();
    }

    public String asString() {
        Charset charset = this.type.getCharset();
        if (charset == null) {
            charset = StandardCharsets.ISO_8859_1;
        }
        return asString(charset);
    }

    /**
     * @since 4.4
     */
    public String asString(final Charset charset) {
        return new String(this.raw, charset);
    }

    public InputStream asStream() {
        return new ByteArrayInputStream(this.raw);
    }

    @Override
    public String toString() {
        return asString();
    }

}
