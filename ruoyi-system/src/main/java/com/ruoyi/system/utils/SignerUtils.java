package com.ruoyi.system.utils;

import org.springframework.security.jwt.codec.Codecs;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.security.jwt.crypto.sign.MacSigner;

import java.nio.CharBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SignerUtils {
    public static boolean verify(String token){
        int firstPeriod = token.indexOf(46);
        int lastPeriod = token.lastIndexOf(46);
        if (firstPeriod > 0 && lastPeriod > firstPeriod) {
            CharBuffer buffer = CharBuffer.wrap(token, 0, firstPeriod);
            byte[] bytes = Codecs.b64UrlDecode(buffer.toString());
            JwtHeader header = new JwtHeader(bytes, new HeaderParameters(parseMap(Codecs.utf8Decode(bytes))));

            buffer.limit(lastPeriod).position(firstPeriod + 1);
            byte[] claims = Codecs.b64UrlDecode(buffer);
            boolean emptyCrypto = lastPeriod == token.length() - 1;
            byte[] crypto;
            buffer.limit(token.length()).position(lastPeriod + 1);
            crypto = Codecs.b64UrlDecode(buffer);
            byte[] content = Codecs.concat(new byte[][]{Codecs.b64UrlEncode(header.bytes()), Codecs.utf8Encode("."), Codecs.b64UrlEncode(claims)});

            MacSigner macSigner = new MacSigner("inspurhealth");
            byte[] signed = macSigner.sign(content);
            if (!isEqual(signed, crypto)) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static Map<String, String> parseMap(String json) {
        if (json != null) {
            json = json.trim();
            if (json.startsWith("{")) {
                return parseMapInternal(json);
            }

            if (json.equals("")) {
                return new LinkedHashMap();
            }
        }

        throw new IllegalArgumentException("Invalid JSON (null)");
    }

    private static Map<String, String> parseMapInternal(String json) {
        Map<String, String> map = new LinkedHashMap();
        json = trimLeadingCharacter(trimTrailingCharacter(json, '}'), '{');
        String[] var5;
        int var4 = (var5 = json.split(",")).length;

        for(int var3 = 0; var3 < var4; ++var3) {
            String pair = var5[var3];
            String[] values = pair.split(":");
            String key = strip(values[0], '"');
            String value = null;
            if (values.length > 0) {
                value = strip(values[1], '"');
            }

            if (map.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate '" + key + "' field");
            }

            map.put(key, value);
        }

        return map;
    }

    private static String strip(String string, char c) {
        return trimLeadingCharacter(trimTrailingCharacter(string.trim(), c), c);
    }

    private static String trimTrailingCharacter(String string, char c) {
        return string.length() >= 0 && string.charAt(string.length() - 1) == c ? string.substring(0, string.length() - 1) : string;
    }

    private static String trimLeadingCharacter(String string, char c) {
        return string.length() >= 0 && string.charAt(0) == c ? string.substring(1) : string;
    }

    private static byte[] serializeParams(HeaderParameters params) {
        StringBuilder builder = new StringBuilder("{");
        appendField(builder, "alg", params.alg);
        params.getClass();
        if ("JWT" != null) {
            params.getClass();
            appendField(builder, "typ", "JWT");
        }

        Iterator var3 = params.map.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var3.next();
            appendField(builder, (String)entry.getKey(), (String)entry.getValue());
        }

        builder.append("}");
        return Codecs.utf8Encode(builder.toString());
    }

    private static void appendField(StringBuilder builder, String name, String value) {
        if (builder.length() > 1) {
            builder.append(",");
        }

        builder.append("\"").append(name).append("\":\"").append(value).append("\"");
    }

    public static boolean isEqual(byte[] b1, byte[] b2) {
        if (b1.length != b2.length) {
            return false;
        } else {
            int xor = 0;

            for(int i = 0; i < b1.length; ++i) {
                xor |= b1[i] ^ b2[i];
            }

            return xor == 0;
        }
    }

    static class HeaderParameters {
        public final String alg;
        public final Map<String, String> map;
        public final String typ;

        HeaderParameters(String alg) {
            this((Map)(new LinkedHashMap(Collections.singletonMap("alg", alg))));
        }

        public HeaderParameters(Map<String, String> map) {
            this.typ = "JWT";
            String alg = (String)map.get("alg");
            String typ = (String)map.get("typ");
            if (typ != null && !"JWT".equalsIgnoreCase(typ)) {
                throw new IllegalArgumentException("typ is not \"JWT\"");
            } else {
                map.remove("alg");
                map.remove("typ");
                this.map = map;
                if (alg == null) {
                    throw new IllegalArgumentException("alg is required");
                } else {
                    this.alg = alg;
                }
            }
        }
    }


    static class JwtHeader {
        private final byte[] bytes;
        final HeaderParameters parameters;

        public JwtHeader(byte[] bytes, HeaderParameters parameters) {
            this.bytes = bytes;
            this.parameters = parameters;
        }

        public byte[] bytes() {
            return this.bytes;
        }

        public String toString() {
            return Codecs.utf8Decode(this.bytes);
        }
    }

}
