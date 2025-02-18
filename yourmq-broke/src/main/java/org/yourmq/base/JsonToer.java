//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;

import org.yourmq.common.Feature;
import org.yourmq.utils.DateUtil;
import org.yourmq.utils.IOUtil;
import org.yourmq.utils.TypeUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;

public class JsonToer implements Toer {
    private static final ThData<StringBuilder> tlBuilder = new ThData(() -> {
        return new StringBuilder(5120);
    });

    public JsonToer() {
    }

    public static void clear() {
        tlBuilder.remove();
    }

    @Override
    public void handle(Context ctx) {
        ONode o = (ONode) ctx.source;
        if (null != o) {
            StringBuilder sb = null;
            if (ctx.options.hasFeature(Feature.DisThreadLocal)) {
                sb = new StringBuilder(5120);
            } else {
                sb = (StringBuilder) tlBuilder.get();
                sb.setLength(0);
            }

            ctx.pretty = ctx.options.hasFeature(Feature.PrettyFormat);
            this.analyse(ctx, o, sb);
            ctx.target = sb.toString();
        }

    }

    public void analyse(Context ctx, ONode o, StringBuilder sb) {
        if (o != null) {
            switch (o.nodeType()) {
                case Value:
                    this.writeValue(ctx, sb, o.nodeData());
                    break;
                case Array:
                    this.writeArray(ctx, sb, o.nodeData());
                    break;
                case Object:
                    this.writeObject(ctx, sb, o.nodeData());
                    break;
                default:
                    sb.append("null");
            }

        }
    }

    private void writeArray(Context ctx, StringBuilder sBuf, ONodeData d) {
        sBuf.append("[");
        if (d.array.size() > 0) {
            if (ctx.pretty) {
                ++ctx.depth;
            }

            Iterator<ONode> iterator = d.array.iterator();

            while (iterator.hasNext()) {
                if (ctx.pretty) {
                    sBuf.append("\n");
                    this.printDepth(ctx, sBuf);
                }

                ONode sub = (ONode) iterator.next();
                this.analyse(ctx, sub, sBuf);
                if (iterator.hasNext()) {
                    sBuf.append(",");
                }
            }

            if (ctx.pretty) {
                sBuf.append("\n");
                --ctx.depth;
                this.printDepth(ctx, sBuf);
            }
        }

        sBuf.append("]");
    }

    private void writeObject(Context ctx, StringBuilder sBuf, ONodeData d) {
        sBuf.append("{");
        if (d.object.size() > 0) {
            if (ctx.pretty) {
                ++ctx.depth;
            }

            Iterator<String> itr = d.object.keySet().iterator();

            while (itr.hasNext()) {
                String k = (String) itr.next();
                if (ctx.pretty) {
                    sBuf.append("\n");
                    this.printDepth(ctx, sBuf);
                }

                this.writeName(ctx, sBuf, k);
                sBuf.append(":");
                if (ctx.pretty) {
                    sBuf.append(" ");
                }

                this.analyse(ctx, (ONode) d.object.get(k), sBuf);
                if (itr.hasNext()) {
                    sBuf.append(",");
                }
            }

            if (ctx.pretty) {
                sBuf.append("\n");
                --ctx.depth;
                this.printDepth(ctx, sBuf);
            }
        }

        sBuf.append("}");
    }

    private void printDepth(Context ctx, StringBuilder sBuf) {
        if (ctx.depth > 0) {
            for (int i = 0; i < ctx.depth; ++i) {
                sBuf.append("  ");
            }
        }

    }

    private void writeValue(Context ctx, StringBuilder sBuf, ONodeData d) {
        OValue v = d.value;
        switch (v.type()) {
            case Null:
                sBuf.append("null");
                break;
            case String:
                this.writeValString(ctx, sBuf, v.getRawString(), true);
                break;
            case DateTime:
                this.writeValDate(ctx, sBuf, v.getRawDate());
                break;
            case Boolean:
                this.writeValBool(ctx, sBuf, v.getRawBoolean());
                break;
            case Number:
                this.writeValNumber(ctx, sBuf, v.getRawNumber());
                break;
            default:
                sBuf.append(v.getString());
        }

    }

    private void writeName(Context ctx, StringBuilder sBuf, String val) {
        if (ctx.options.hasFeature(Feature.QuoteFieldNames)) {
            if (ctx.options.hasFeature(Feature.UseSingleQuotes)) {
                sBuf.append("'");
                this.writeString(ctx, sBuf, val, '\'');
                sBuf.append("'");
            } else {
                sBuf.append("\"");
                this.writeString(ctx, sBuf, val, '"');
                sBuf.append("\"");
            }
        } else {
            this.writeString(ctx, sBuf, val, '"');
        }

    }

    private void writeValDate(Context ctx, StringBuilder sBuf, Date val) {
        if (ctx.options.hasFeature(Feature.WriteDateUseTicks)) {
            sBuf.append(val.getTime());
        } else if (ctx.options.hasFeature(Feature.WriteDateUseFormat)) {
            String valStr = DateUtil.format(val, ctx.options.getDateFormat(), ctx.options.getTimeZone());
            this.writeValString(ctx, sBuf, valStr, false);
        } else {
            sBuf.append("new Date(").append(val.getTime()).append(")");
        }

    }

    private void writeValBool(Context ctx, StringBuilder sBuf, Boolean val) {
        if (ctx.options.hasFeature(Feature.WriteBoolUse01)) {
            sBuf.append(val ? 1 : 0);
        } else {
            sBuf.append(val ? "true" : "false");
        }

    }

    private void writeValNumber(Context ctx, StringBuilder sBuf, Number val) {
        String sVal;
        if (val instanceof BigInteger) {
            BigInteger v = (BigInteger) val;
            sVal = v.toString();
            if (ctx.options.hasFeature(Feature.WriteNumberUseString)) {
                this.writeValString(ctx, sBuf, sVal, false);
            } else if (sVal.length() > 16 && (v.compareTo(TypeUtil.INT_LOW) < 0 || v.compareTo(TypeUtil.INT_HIGH) > 0) && ctx.options.hasFeature(Feature.BrowserCompatible)) {
                this.writeValString(ctx, sBuf, sVal, false);
            } else {
                sBuf.append(sVal);
            }

        } else if (!(val instanceof BigDecimal)) {
            if (ctx.options.hasFeature(Feature.WriteNumberUseString)) {
                this.writeValString(ctx, sBuf, val.toString(), false);
            } else {
                sBuf.append(val.toString());
            }

        } else {
            BigDecimal v = (BigDecimal) val;
            sVal = v.toPlainString();
            if (ctx.options.hasFeature(Feature.WriteNumberUseString)) {
                this.writeValString(ctx, sBuf, sVal, false);
            } else if (sVal.length() > 16 && (v.compareTo(TypeUtil.DEC_LOW) < 0 || v.compareTo(TypeUtil.DEC_HIGH) > 0) && ctx.options.hasFeature(Feature.BrowserCompatible)) {
                this.writeValString(ctx, sBuf, sVal, false);
            } else {
                sBuf.append(sVal);
            }

        }
    }

    private void writeValString(Context ctx, StringBuilder sBuf, String val, boolean isStr) {
        boolean useSingleQuotes = ctx.options.hasFeature(Feature.UseSingleQuotes);
        char quote = (char) (useSingleQuotes ? 39 : 34);
        sBuf.append((char) quote);
        if (isStr) {
            this.writeString(ctx, sBuf, val, (char) quote);
        } else {
            sBuf.append(val);
        }

        sBuf.append((char) quote);
    }

    private void writeString(Context ctx, StringBuilder sBuf, String val, char quote) {
        boolean isCompatible = ctx.options.hasFeature(Feature.BrowserCompatible);
        boolean isSecure = ctx.options.hasFeature(Feature.BrowserSecure);
        boolean isTransfer = ctx.options.hasFeature(Feature.TransferCompatible);
        int i = 0;

        for (int len = val.length(); i < len; ++i) {
            char c = val.charAt(i);
            if (c == quote || c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == '\b' || c >= 0 && c <= 7) {
                sBuf.append("\\");
                sBuf.append(IOUtil.CHARS_MARK[c]);
            } else if (!isSecure || c != '(' && c != ')' && c != '<' && c != '>') {
                if (isTransfer && c == '\\') {
                    sBuf.append("\\");
                    sBuf.append(IOUtil.CHARS_MARK[c]);
                } else {
                    if (isCompatible) {
                        if (c == '\\') {
                            sBuf.append("\\");
                            sBuf.append(IOUtil.CHARS_MARK[c]);
                            continue;
                        }

                        if (c < ' ') {
                            sBuf.append('\\');
                            sBuf.append('u');
                            sBuf.append('0');
                            sBuf.append('0');
                            sBuf.append(IOUtil.DIGITS[c >>> 4 & 15]);
                            sBuf.append(IOUtil.DIGITS[c & 15]);
                            continue;
                        }

                        if (c >= 127) {
                            sBuf.append('\\');
                            sBuf.append('u');
                            sBuf.append(IOUtil.DIGITS[c >>> 12 & 15]);
                            sBuf.append(IOUtil.DIGITS[c >>> 8 & 15]);
                            sBuf.append(IOUtil.DIGITS[c >>> 4 & 15]);
                            sBuf.append(IOUtil.DIGITS[c & 15]);
                            continue;
                        }
                    }

                    sBuf.append(c);
                }
            } else {
                sBuf.append('\\');
                sBuf.append('u');
                sBuf.append(IOUtil.DIGITS[c >>> 12 & 15]);
                sBuf.append(IOUtil.DIGITS[c >>> 8 & 15]);
                sBuf.append(IOUtil.DIGITS[c >>> 4 & 15]);
                sBuf.append(IOUtil.DIGITS[c & 15]);
            }
        }

    }
}
