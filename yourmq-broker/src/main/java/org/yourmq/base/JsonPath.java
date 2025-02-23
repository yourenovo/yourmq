
package org.yourmq.base;

import org.yourmq.utils.StringUtil;

import java.util.*;
import java.util.regex.Pattern;

public class JsonPath {
    private static int _cacheSize = 1024;
    private static Map<String, JsonPath> _jpathCache = new HashMap(128);
    private static final ThData<CharBuffer> tlBuilder = new ThData(() -> {
        return new CharBuffer();
    });
    private static final ThData<TmpCache> tlCache = new ThData(() -> {
        return new TmpCache();
    });
    private List<Segment> segments = new ArrayList();
    private static Map<String, Pattern> _regexLib = new HashMap();
    private static Resolver handler_$ = (bef, s, regroup, root, tmp, usd, orNew) -> {
        return tmp;
    };
    private static Resolver handler_xx = (bef, s, regroup, root, tmp, usd, orNew) -> {
        if (s.name.length() > 0) {
            ONode tmp2 = (new ONode((ONode) null, root.options())).asArray();
            if ("*".equals(s.name)) {
                scanByAll(s.name, tmp, true, tmp2.ary());
            } else if (s.name.startsWith("?")) {
                scanByExpr(tmp, tmp2.ary(), bef, s, regroup, root, tmp, usd, orNew);
            } else {
                scanByName(s.name, tmp, tmp2.ary());
            }

            if (tmp2.count() > 0) {
                return tmp2;
            }
        }

        return null;
    };
    private static Resolver handler_x = (bef, s, regroup, root, tmp, usd, orNew) -> {
        ONode tmp2 = null;
        if (tmp.count() > 0) {
            tmp2 = (new ONode((ONode) null, tmp.options())).asArray();
            if (tmp.isObject()) {
                tmp2.addAll(tmp.obj().values());
            } else if (tmp.isArray()) {
                if (regroup) {
                    Iterator var8 = tmp.ary().iterator();

                    while (var8.hasNext()) {
                        ONode n1 = (ONode) var8.next();
                        if (n1.isObject()) {
                            tmp2.addAll(n1.obj().values());
                        } else {
                            tmp2.addAll(n1.ary());
                        }
                    }
                } else {
                    tmp2.addAll(tmp.ary());
                }
            }
        }

        return tmp2;
    };
    private static Resolver handler_prop = (bef, s, regroup, root, tmp, usd, crud) -> {
        if (tmp.isObject()) {
            return crud == JsonPath.CRUD.GET_OR_NEW ? tmp.getOrNew(s.cmd) : tmp.getOrNull(s.cmd);
        } else if (!tmp.isArray()) {
            return crud == JsonPath.CRUD.GET_OR_NEW && tmp.isNull() ? tmp.getOrNew(s.cmd) : null;
        } else {
            ONode tmp2 = (new ONode((ONode) null, tmp.options())).asArray();
            Iterator var8 = tmp.ary().iterator();

            while (true) {
                ONode n1;
                do {
                    do {
                        if (!var8.hasNext()) {
                            return tmp2;
                        }

                        n1 = (ONode) var8.next();
                        if (n1.isObject()) {
                            if (crud == JsonPath.CRUD.GET_OR_NEW) {
                                tmp2.add(n1.getOrNew(s.cmd));
                            } else {
                                ONode n2x = (ONode) n1.nodeData().object.get(s.cmd);
                                if (n2x != null) {
                                    tmp2.add(n2x);
                                }
                            }
                        }
                    } while (!regroup);
                } while (!n1.isArray());

                Iterator var13 = n1.ary().iterator();

                while (var13.hasNext()) {
                    ONode n2 = (ONode) var13.next();
                    if (n2.isObject()) {
                        if (crud == JsonPath.CRUD.GET_OR_NEW) {
                            tmp2.add(n2.getOrNew(s.cmd));
                        } else {
                            ONode n3 = (ONode) n2.nodeData().object.get(s.cmd);
                            if (n3 != null) {
                                tmp2.add(n3);
                            }
                        }
                    }
                }
            }
        }
    };
    private static Resolver handler_fun = (bef, s, regroup, root, tmp, usd, crud) -> {
        ONode max_n;
        Iterator var10;
        double sum;
        Iterator var18;
        ONode n1x;
        ONode n2xx;
        switch (s.cmd) {
            case "size()":
                return (new ONode((ONode) null, tmp.options())).val(tmp.count());
            case "length()":
                if (tmp.isValue()) {
                    return (new ONode((ONode) null, tmp.options())).val(tmp.getString().length());
                }

                return (new ONode((ONode) null, tmp.options())).val(tmp.count());
            case "keys()":
                if (tmp.isObject()) {
                    return (new ONode((ONode) null, tmp.options())).addAll(tmp.obj().keySet());
                }

                return null;
            case "min()":
                if (!tmp.isArray()) {
                    return null;
                } else if (tmp.count() == 0) {
                    return null;
                } else {
                    max_n = null;
                    var10 = tmp.ary().iterator();

                    while (true) {
                        do {
                            do {
                                if (!var10.hasNext()) {
                                    return max_n;
                                }

                                n1x = (ONode) var10.next();
                                if (n1x.isValue()) {
                                    if (max_n == null) {
                                        max_n = n1x;
                                    } else if (n1x.getDouble() < max_n.getDouble()) {
                                        max_n = n1x;
                                    }
                                }
                            } while (!regroup);
                        } while (!n1x.isArray());

                        var18 = n1x.ary().iterator();

                        while (var18.hasNext()) {
                            n2xx = (ONode) var18.next();
                            if (n2xx.isValue()) {
                                if (max_n == null) {
                                    max_n = n2xx;
                                } else if (n2xx.getDouble() < max_n.getDouble()) {
                                    max_n = n2xx;
                                }
                            }
                        }
                    }
                }
            case "max()":
                if (!tmp.isArray()) {
                    return null;
                } else if (tmp.count() == 0) {
                    return null;
                } else {
                    max_n = null;
                    var10 = tmp.ary().iterator();

                    while (true) {
                        do {
                            do {
                                if (!var10.hasNext()) {
                                    return max_n;
                                }

                                n1x = (ONode) var10.next();
                                if (n1x.isValue()) {
                                    if (max_n == null) {
                                        max_n = n1x;
                                    } else if (n1x.getDouble() > max_n.getDouble()) {
                                        max_n = n1x;
                                    }
                                }
                            } while (!regroup);
                        } while (!n1x.isArray());

                        var18 = n1x.ary().iterator();

                        while (var18.hasNext()) {
                            n2xx = (ONode) var18.next();
                            if (n2xx.isValue()) {
                                if (max_n == null) {
                                    max_n = n2xx;
                                } else if (n2xx.getDouble() > max_n.getDouble()) {
                                    max_n = n2xx;
                                }
                            }
                        }
                    }
                }
            case "avg()":
                if (tmp.isArray()) {
                    if (tmp.count() == 0) {
                        return null;
                    }

                    sum = 0.0;
                    int num = 0;
                    var18 = tmp.ary().iterator();

                    while (true) {
                        do {
                            do {
                                if (!var18.hasNext()) {
                                    if (num > 0) {
                                        return (new ONode((ONode) null, tmp.options())).val(sum / (double) num);
                                    }

                                    return null;
                                }

                                n2xx = (ONode) var18.next();
                                if (n2xx.isValue()) {
                                    sum += n2xx.getDouble();
                                    ++num;
                                }
                            } while (!regroup);
                        } while (!n2xx.isArray());

                        Iterator var21 = n2xx.ary().iterator();

                        while (var21.hasNext()) {
                            ONode n2x = (ONode) var21.next();
                            if (n2x.isValue()) {
                                sum += n2x.getDouble();
                                ++num;
                            }
                        }
                    }
                }

                return null;
            case "sum()":
                if (!tmp.isArray()) {
                    return null;
                } else if (tmp.count() == 0) {
                    return null;
                } else {
                    sum = 0.0;
                    Iterator var11 = tmp.ary().iterator();

                    while (true) {
                        ONode n1;
                        do {
                            do {
                                if (!var11.hasNext()) {
                                    return (new ONode((ONode) null, tmp.options())).val(sum);
                                }

                                n1 = (ONode) var11.next();
                                if (n1.isValue()) {
                                    sum += n1.getDouble();
                                }
                            } while (!regroup);
                        } while (!n1.isArray());

                        Iterator var13 = n1.ary().iterator();

                        while (var13.hasNext()) {
                            ONode n2 = (ONode) var13.next();
                            if (n2.isValue()) {
                                sum += n2.getDouble();
                            }
                        }
                    }
                }
            case "first()":
                if (tmp.isArray()) {
                    if (tmp.count() == 0) {
                        return null;
                    }

                    max_n = tmp.get(0);
                    if (regroup && max_n.isArray()) {
                        return max_n.get(0);
                    }

                    return max_n;
                }

                return null;
            case "last()":
                if (tmp.isArray()) {
                    if (tmp.count() == 0) {
                        return null;
                    }

                    max_n = tmp.get(tmp.count() - 1);
                    if (regroup && max_n.isArray()) {
                        return max_n.get(max_n.count() - 1);
                    }

                    return max_n;
                }

                return null;
            default:
                return null;
        }
    };
    private static Resolver handler_ary_x = (bef, s, regroup, root, tmp, usd, crud) -> {
        ONode tmp2 = null;
        if (tmp.isArray()) {
            if (regroup) {
                tmp2 = (new ONode((ONode) null, tmp.options())).asArray();
                Iterator var8 = tmp.ary().iterator();

                while (var8.hasNext()) {
                    ONode n1 = (ONode) var8.next();
                    if (n1.isObject()) {
                        tmp2.addAll(n1.obj().values());
                    } else {
                        tmp2.addAll(n1.ary());
                    }
                }
            } else {
                tmp2 = tmp;
            }
        }

        if (tmp.isObject()) {
            tmp2 = (new ONode((ONode) null, tmp.options())).asArray();
            tmp2.addAll(tmp.obj().values());
        }

        return tmp2;
    };
    private static Resolver handler_ary_exp = (bef, s, regroup, root, tmp, usd, crud) -> {
        ONode tmp2 = tmp;
        Iterator var8;
        ONode n1;
        Iterator var10;
        ONode n2;
        if (s.op == null) {
            if (tmp.isObject()) {
                if (evalDo(tmp, s.left, true, usd, crud).isNull()) {
                    return null;
                }
            } else {
                if (!tmp.isArray()) {
                    return null;
                }

                tmp2 = (new ONode((ONode) null, tmp.options())).asArray();
                var8 = tmp.ary().iterator();

                while (true) {
                    do {
                        do {
                            if (!var8.hasNext()) {
                                return tmp2;
                            }

                            n1 = (ONode) var8.next();
                            if (n1.isObject() && !evalDo(n1, s.left, true, usd, crud).isNull()) {
                                tmp2.nodeData().array.add(n1);
                            }
                        } while (!regroup);
                    } while (!n1.isArray());

                    var10 = n1.ary().iterator();

                    while (var10.hasNext()) {
                        n2 = (ONode) var10.next();
                        if (n2.isObject() && !evalDo(n2, s.left, true, usd, crud).isNull()) {
                            tmp2.nodeData().array.add(n2);
                        }
                    }
                }
            }
        } else if (tmp.isObject()) {
            if ("@".equals(s.left)) {
                return null;
            }

            ONode leftOx = evalDo(tmp, s.left, true, usd, crud);
            if (!compare(root, tmp, leftOx, s.op, s.right, usd, crud)) {
                return null;
            }
        } else if (tmp.isArray()) {
            tmp2 = (new ONode((ONode) null, tmp.options())).asArray();
            if ("@".equals(s.left)) {
                var8 = tmp.ary().iterator();

                while (true) {
                    while (var8.hasNext()) {
                        n1 = (ONode) var8.next();
                        if (n1.isArray()) {
                            var10 = n1.ary().iterator();

                            while (var10.hasNext()) {
                                n2 = (ONode) var10.next();
                                if (compare(root, n2, n2, s.op, s.right, usd, crud)) {
                                    tmp2.addNode(n2);
                                }
                            }
                        } else if (compare(root, n1, n1, s.op, s.right, usd, crud)) {
                            tmp2.addNode(n1);
                        }
                    }

                    return tmp2;
                }
            } else {
                var8 = tmp.ary().iterator();

                while (true) {
                    while (var8.hasNext()) {
                        n1 = (ONode) var8.next();
                        if (n1.isArray()) {
                            var10 = n1.ary().iterator();

                            while (var10.hasNext()) {
                                n2 = (ONode) var10.next();
                                ONode leftO = evalDo(n2, s.left, true, usd, crud);
                                if (compare(root, n2, leftO, s.op, s.right, usd, crud)) {
                                    tmp2.addNode(n2);
                                }
                            }
                        } else {
                            ONode leftOxx = evalDo(n1, s.left, true, usd, crud);
                            if (compare(root, n1, leftOxx, s.op, s.right, usd, crud)) {
                                tmp2.addNode(n1);
                            }
                        }
                    }

                    return tmp2;
                }
            }
        } else if (tmp.isValue() && "@".equals(s.left) && !compare(root, tmp, tmp, s.op, s.right, usd, crud)) {
            return null;
        }

        return tmp2;
    };
    private static Resolver handler_ary_ref = (bef, s, regroup, root, tmp, usd, crud) -> {
        ONode tmp2 = null;
        if (tmp.isObject()) {
            if (s.cmdAry.startsWith("$")) {
                tmp2 = evalDo(root, s.cmdAry, true, usd, crud);
            } else {
                tmp2 = evalDo(tmp, s.cmdAry, true, usd, crud);
            }

            if (tmp2.isValue()) {
                tmp2 = tmp.get(tmp2.getString());
            } else {
                tmp2 = null;
            }
        }

        return tmp2;
    };
    private static Resolver handler_ary_multi = (bef, s, regroup, root, tmp, usd, crud) -> {
        ONode tmp2 = null;
        Iterator var16;
        if (s.cmdAry.indexOf("'") >= 0) {
            Iterator var8;
            if (tmp.isObject()) {
                var8 = s.nameS.iterator();

                while (var8.hasNext()) {
                    String kx = (String) var8.next();
                    ONode n1x = (ONode) tmp.obj().get(kx);
                    if (n1x != null) {
                        if (tmp2 == null) {
                            tmp2 = (new ONode((ONode) null, tmp.options())).asArray();
                        }

                        tmp2.addNode(n1x);
                    }
                }
            }

            if (tmp.isArray()) {
                tmp2 = (new ONode((ONode) null, tmp.options())).asArray();
                var8 = tmp.ary().iterator();

                while (true) {
                    ONode tmp1;
                    do {
                        if (!var8.hasNext()) {
                            return tmp2;
                        }

                        tmp1 = (ONode) var8.next();
                    } while (!tmp1.isObject());

                    var16 = s.nameS.iterator();

                    while (var16.hasNext()) {
                        String k = (String) var16.next();
                        ONode n1 = (ONode) tmp1.obj().get(k);
                        if (n1 != null) {
                            tmp2.addNode(n1);
                        }
                    }
                }
            }
        } else if (tmp.isArray()) {
            List<ONode> list2 = tmp.nodeData().array;
            int len2 = list2.size();
            var16 = s.indexS.iterator();

            while (var16.hasNext()) {
                int idx = (Integer) var16.next();
                if (idx >= 0 && idx < len2) {
                    if (tmp2 == null) {
                        tmp2 = (new ONode((ONode) null, tmp.options())).asArray();
                    }

                    tmp2.addNode((ONode) list2.get(idx));
                }
            }
        }

        return tmp2;
    };
    private static Resolver handler_ary_range = (bef, s, regroup, root, tmp, usd, crud) -> {
        if (tmp.isArray()) {
            int count = tmp.count();
            int start = s.start;
            int end = s.end;
            if (start < 0) {
                start += count;
            }

            if (end == 0) {
                end = count;
            }

            if (end < 0) {
                end += count;
            }

            if (start < 0) {
                start = 0;
            }

            if (end > count) {
                end = count;
            }

            return (new ONode((ONode) null, tmp.options())).addAll(tmp.ary().subList(start, end));
        } else {
            return null;
        }
    };
    private static Resolver handler_ary_prop = (bef, s, regroup, root, tmp, usd, crud) -> {
        ONode tmp2;
        Iterator var8;
        ONode n1;
        ONode n2;
        if (s.cmdHasQuote) {
            if (tmp.isObject()) {
                return tmp.getOrNull(s.name);
            } else if (tmp.isArray()) {
                tmp2 = (new ONode((ONode) null, tmp.options())).asArray();
                var8 = tmp.ary().iterator();

                while (var8.hasNext()) {
                    n1 = (ONode) var8.next();
                    if (n1.isObject()) {
                        n2 = (ONode) n1.nodeData().object.get(s.name);
                        if (n2 != null) {
                            tmp2.add(n2);
                        }
                    }
                }

                return tmp2;
            } else {
                return null;
            }
        } else if (regroup) {
            tmp2 = (new ONode((ONode) null, tmp.options())).asArray();
            var8 = tmp.ary().iterator();

            while (var8.hasNext()) {
                n1 = (ONode) var8.next();
                n2 = null;
                if (s.start < 0) {
                    if (crud == JsonPath.CRUD.GET_OR_NEW) {
                        n2 = n1.getOrNew(n1.count() + s.start);
                    } else {
                        n2 = n1.getOrNull(n1.count() + s.start);
                    }
                } else if (crud == JsonPath.CRUD.GET_OR_NEW) {
                    n2 = n1.getOrNew(s.start);
                } else {
                    n2 = n1.getOrNull(s.start);
                }

                if (n2 != null) {
                    tmp2.add(n2);
                }
            }

            return tmp2;
        } else if (s.start < 0) {
            return crud == JsonPath.CRUD.GET_OR_NEW ? tmp.getOrNew(tmp.count() + s.start) : tmp.getOrNull(tmp.count() + s.start);
        } else {
            return crud == JsonPath.CRUD.GET_OR_NEW ? tmp.getOrNew(s.start) : tmp.getOrNull(s.start);
        }
    };

    public static ONode eval(ONode source, String jpath, boolean useStandard, boolean cacheJpath) {
        return eval(source, jpath, useStandard, cacheJpath, JsonPath.CRUD.GET);
    }

    public static ONode eval(ONode source, String jpath, boolean useStandard, boolean cacheJpath, CRUD crud) {
        ((TmpCache) tlCache.get()).clear();
        return evalDo(source, jpath, cacheJpath, useStandard, crud);
    }

    private static ONode evalDo(ONode source, String jpath, boolean cacheJpath, boolean useStandard, CRUD crud) {
        JsonPath jsonPath = null;
        if (cacheJpath) {
            jsonPath = (JsonPath) _jpathCache.get(jpath);
            if (jsonPath == null) {
                synchronized (jpath.intern()) {
                    jsonPath = (JsonPath) _jpathCache.get(jpath);
                    if (jsonPath == null) {
                        jsonPath = compile(jpath);
                        if (_jpathCache.size() < _cacheSize) {
                            _jpathCache.put(jpath, jsonPath);
                        }
                    }
                }
            }
        } else {
            jsonPath = compile(jpath);
        }

        return exec(jsonPath, source, useStandard, crud);
    }

    public static void resolvePath(String parentPath, ONode parentNode) {
        parentNode.attrSet("$PATH", parentPath);
        if (parentNode.isArray()) {
            for (int i = 0; i < parentNode.count(); ++i) {
                resolvePath(parentPath + "[" + i + "]", parentNode.get(i));
            }
        } else if (parentNode.isObject()) {
            Iterator var4 = parentNode.obj().entrySet().iterator();

            while (var4.hasNext()) {
                Map.Entry<String, ONode> kv = (Map.Entry) var4.next();
                resolvePath(parentPath + "['" + (String) kv.getKey() + "']", (ONode) kv.getValue());
            }
        }

    }

    public static void extractPath(List<String> paths, ONode oNode) {
        String path = oNode.attrGet("$PATH");
        if (StringUtil.isEmpty(path)) {
            if (oNode.isArray()) {
                for (int i = 0; i < oNode.count(); ++i) {
                    extractPath(paths, oNode.get(i));
                }
            } else if (oNode.isObject()) {
                Iterator var5 = oNode.obj().entrySet().iterator();

                while (var5.hasNext()) {
                    Map.Entry<String, ONode> kv = (Map.Entry) var5.next();
                    extractPath(paths, (ONode) kv.getValue());
                }
            }
        } else {
            paths.add(path);
        }

    }

    public static void clear() {
        tlBuilder.remove();
        tlCache.remove();
    }

    private JsonPath() {
    }

    private static JsonPath compile(String jpath) {
        String jpath2 = jpath.replace("..", ".^");
        JsonPath jsonPath = new JsonPath();
        char token = 0;

        char c_last = 0;
        CharBuffer buffer = (CharBuffer) tlBuilder.get();
        buffer.setLength(0);
        CharReader reader = new CharReader(jpath2);

        while (true) {
            char c = reader.next();
            if (c == 0) {
                if (buffer.length() > 0) {
                    jsonPath.segments.add(new Segment(buffer.toString()));
                    buffer.clear();
                }

                return jsonPath;
            }

            switch (c) {
                case '(':
                    if (token == '[') {
                        token = c;
                    }

                    buffer.append(c);
                    break;
                case ')':
                    if (token == '(') {
                        token = c;
                    }

                    buffer.append(c);
                    break;
                case '.':
                    if (token > 0) {
                        buffer.append(c);
                    } else if (buffer.length() > 0) {
                        jsonPath.segments.add(new Segment(buffer.toString()));
                        buffer.clear();
                    }
                    break;
                case '[':
                    if (token == 0) {
                        token = c;
                        if (buffer.length() > 0 && c_last != '^') {
                            jsonPath.segments.add(new Segment(buffer.toString()));
                            buffer.clear();
                        }
                    } else {
                        buffer.append(c);
                    }
                    break;
                case ']':
                    if (token != '[' && token != ')') {
                        buffer.append(c);
                    } else {
                        token = 0;
                        buffer.append(c);
                        if (buffer.length() > 0) {
                            jsonPath.segments.add(new Segment(buffer.toString()));
                            buffer.clear();
                        }
                    }
                    break;
                default:
                    buffer.append(c);
            }

            c_last = c;
        }
    }

    private static ONode exec(JsonPath jsonPath, ONode source, boolean useStandard, CRUD crud) {
        ONode tmp = source;
        Segment last = null;
        boolean branch_do = false;
        boolean regroup = false;

        Segment s;
        for (Iterator var8 = jsonPath.segments.iterator(); var8.hasNext(); last = s) {
            s = (Segment) var8.next();
            if (tmp == null) {
                break;
            }

            if (!branch_do || !useStandard && s.cmdAry == null) {
                tmp = s.handler.run(last, s, regroup, source, tmp, useStandard, crud);
                branch_do = s.cmdHasUnline;
            } else {
                ONode tmp2 = (new ONode((ONode) null, source.options())).asArray();
                Iterator var11 = tmp.ary().iterator();

                while (var11.hasNext()) {
                    ONode n1 = (ONode) var11.next();
                    ONode n2 = s.handler.run(last, s, regroup, source, n1, useStandard, crud);
                    if (n2 != null) {
                        if (s.cmdAry != null) {
                            if (n2.isArray()) {
                                tmp2.addAll(n2.ary());
                            } else {
                                tmp2.addNode(n2);
                            }
                        } else {
                            tmp2.addNode(n2);
                        }
                    }
                }

                tmp = tmp2;
                if (!useStandard) {
                    branch_do = false;
                }
            }

            if (s.regroup) {
                regroup = true;
            } else if (s.ranged) {
                regroup = false;
            }
        }

        return tmp == null ? new ONode((ONode) null, source.options()) : tmp;
    }

    private static void scanByExpr(ONode source, List<ONode> target, Segment bef, Segment s, Boolean regroup, ONode root, ONode tmp, Boolean usd, CRUD crud) {
        if (source.isObject()) {
            ONode tmp2 = handler_ary_exp.run(bef, s, regroup, root, source, usd, crud);
            if (tmp2 != null) {
                target.add(tmp2);
            }

            Iterator var12 = source.obj().entrySet().iterator();

            while (var12.hasNext()) {
                Map.Entry<String, ONode> kv = (Map.Entry) var12.next();
                scanByExpr((ONode) kv.getValue(), target, bef, s, regroup, root, tmp, usd, crud);
            }

        } else if (source.isArray()) {
            Iterator var9 = source.ary().iterator();

            while (var9.hasNext()) {
                ONode n1 = (ONode) var9.next();
                scanByExpr(n1, target, bef, s, regroup, root, tmp, usd, crud);
            }

        }
    }

    private static void scanByName(String name, ONode source, List<ONode> target) {
        Iterator var3;
        if (source.isObject()) {
            Map.Entry kv;
            for (var3 = source.obj().entrySet().iterator(); var3.hasNext(); scanByName(name, (ONode) kv.getValue(), target)) {
                kv = (Map.Entry) var3.next();
                if (name.equals(kv.getKey())) {
                    target.add((ONode) kv.getValue());
                }
            }

        } else if (source.isArray()) {
            var3 = source.ary().iterator();

            while (var3.hasNext()) {
                ONode n1 = (ONode) var3.next();
                scanByName(name, n1, target);
            }

        }
    }

    private static void scanByAll(String name, ONode source, boolean isRoot, List<ONode> target) {
        if (!isRoot) {
            target.add(source);
        }

        Iterator var4;
        if (source.isObject()) {
            var4 = source.obj().entrySet().iterator();

            while (var4.hasNext()) {
                Map.Entry<String, ONode> kv = (Map.Entry) var4.next();
                scanByAll(name, (ONode) kv.getValue(), false, target);
            }

        } else if (source.isArray()) {
            var4 = source.ary().iterator();

            while (var4.hasNext()) {
                ONode n1 = (ONode) var4.next();
                scanByAll(name, n1, false, target);
            }

        }
    }

    private static boolean compare(ONode root, ONode parent, ONode leftO, String op, String right, boolean useStandard, CRUD crud) {
        if (leftO == null) {
            return false;
        } else if (leftO.isValue() && !leftO.val().isNull()) {
            OValue left = leftO.val();
            ONode rightO = null;
            if (right.startsWith("$")) {
                rightO = (ONode) ((TmpCache) tlCache.get()).get(right);
                if (rightO == null) {
                    rightO = evalDo(root, right, true, useStandard, crud);
                    ((TmpCache) tlCache.get()).put(right, rightO);
                }
            }

            if (right.startsWith("@")) {
                rightO = evalDo(parent, right, true, useStandard, crud);
            }

            if (rightO != null) {
                if (rightO.isValue()) {
                    if (rightO.val().type() == OValueType.String) {
                        right = "'" + rightO.getString() + "'";
                    } else {
                        right = rightO.getDouble() + "";
                    }
                } else {
                    right = null;
                }
            }

            Object val;
            Iterator var12;
            ONode n1;
            switch (op) {
                case "==":
                    if (right == null) {
                        return false;
                    } else {
                        if (right.startsWith("'")) {
                            return left.getString().equals(right.substring(1, right.length() - 1));
                        }

                        return left.getDouble() == Double.parseDouble(right);
                    }
                case "!=":
                    if (right == null) {
                        return false;
                    } else {
                        if (right.startsWith("'")) {
                            return !left.getString().equals(right.substring(1, right.length() - 1));
                        }

                        return left.getDouble() != Double.parseDouble(right);
                    }
                case "<":
                    if (right == null) {
                        return false;
                    }

                    return left.getDouble() < Double.parseDouble(right);
                case "<=":
                    if (right == null) {
                        return false;
                    }

                    return left.getDouble() <= Double.parseDouble(right);
                case ">":
                    if (right == null) {
                        return false;
                    }

                    return left.getDouble() > Double.parseDouble(right);
                case ">=":
                    if (right == null) {
                        return false;
                    }

                    return left.getDouble() >= Double.parseDouble(right);
                case "=~":
                    if (right == null) {
                        return false;
                    }

                    int end = right.lastIndexOf(47);
                    String exp = right.substring(1, end);
                    return regex(right, exp).matcher(left.getString()).find();
                case "in":
                    if (right == null) {
                        val = left.getRaw();
                        var12 = rightO.ary().iterator();

                        do {
                            if (!var12.hasNext()) {
                                return false;
                            }

                            n1 = (ONode) var12.next();
                        } while (!n1.val().getRaw().equals(val));

                        return true;
                    } else {
                        if (right.indexOf("'") > 0) {
                            return getStringAry(right).contains(left.getString());
                        }

                        return getDoubleAry(right).contains(left.getDouble());
                    }
                case "nin":
                    if (right == null) {
                        val = left.getRaw();
                        var12 = rightO.ary().iterator();

                        do {
                            if (!var12.hasNext()) {
                                return true;
                            }

                            n1 = (ONode) var12.next();
                        } while (!n1.val().getRaw().equals(val));

                        return false;
                    } else {
                        if (right.indexOf("'") > 0) {
                            return !getStringAry(right).contains(left.getString());
                        }

                        return !getDoubleAry(right).contains(left.getDouble());
                    }
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    private static List<String> getStringAry(String text) {
        List<String> ary = new ArrayList();
        String test2 = text.substring(1, text.length() - 1);
        String[] ss = test2.split(",");
        String[] var4 = ss;
        int var5 = ss.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            String s = var4[var6];
            ary.add(s.substring(1, s.length() - 1));
        }

        return ary;
    }

    private static List<Double> getDoubleAry(String text) {
        List<Double> ary = new ArrayList();
        String test2 = text.substring(1, text.length() - 1);
        String[] ss = test2.split(",");
        String[] var4 = ss;
        int var5 = ss.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            String s = var4[var6];
            ary.add(Double.parseDouble(s));
        }

        return ary;
    }

    private static Pattern regex(String exprFull, String expr) {
        Pattern p = (Pattern) _regexLib.get(exprFull);
        if (p == null) {
            synchronized (exprFull.intern()) {
                if (p == null) {
                    if (exprFull.endsWith("i")) {
                        p = Pattern.compile(expr, 2);
                    } else {
                        p = Pattern.compile(expr);
                    }

                    _regexLib.put(exprFull, p);
                }
            }
        }

        return p;
    }

    public static enum CRUD {
        GET,
        GET_OR_NEW,
        REMOVE;

        private CRUD() {
        }
    }

    private static class Segment {
        public final String cmd;
        public String cmdAry;
        public final boolean cmdHasQuote;
        public final boolean cmdHasUnline;
        public final boolean regroup;
        public List<Integer> indexS;
        public List<String> nameS;
        public String name;
        public int start = 0;
        public int end = 0;
        public boolean ranged = false;
        public String left;
        public String op;
        public String right;
        public Resolver handler;

        public Segment(String test) {
            this.cmd = test.trim();
            this.cmdHasQuote = this.cmd.indexOf("'") >= 0;
            this.cmdHasUnline = this.cmd.startsWith("^");
            this.regroup = this.cmd.contains("?") || this.cmd.startsWith("*");
            if (this.cmdHasUnline) {
                this.name = this.cmd.substring(1);
            }

            if (this.cmd.endsWith("]")) {
                if (this.cmdHasUnline) {
                    this.cmdAry = this.cmd.substring(1, this.cmd.length() - 1).trim();
                } else {
                    this.cmdAry = this.cmd.substring(0, this.cmd.length() - 1).trim();
                }

                String[] ss2;
                if (this.cmdAry.startsWith("?")) {
                    String s2 = this.cmdAry.substring(2, this.cmdAry.length() - 1);
                    ss2 = s2.split(" ");
                    this.left = ss2[0];
                    if (ss2.length == 3) {
                        this.op = ss2[1];
                        this.right = ss2[2];
                    }
                } else {
                    String[] iAry;
                    if (this.cmdAry.indexOf(":") >= 0) {
                        iAry = this.cmdAry.split(":", -1);
                        this.start = 0;
                        if (iAry[0].length() > 0) {
                            this.start = Integer.parseInt(iAry[0]);
                        }

                        this.end = 0;
                        if (iAry[1].length() > 0) {
                            this.end = Integer.parseInt(iAry[1]);
                        }

                        this.ranged = true;
                    } else if (this.cmdAry.indexOf(",") > 0) {
                        int var4;
                        int var5;
                        String i1;
                        if (this.cmdAry.indexOf("'") >= 0) {
                            this.nameS = new ArrayList();
                            iAry = this.cmdAry.split(",");
                            ss2 = iAry;
                            var4 = iAry.length;

                            for (var5 = 0; var5 < var4; ++var5) {
                                i1 = ss2[var5];
                                i1 = i1.trim();
                                this.nameS.add(i1.substring(1, i1.length() - 1));
                            }
                        } else {
                            this.indexS = new ArrayList();
                            iAry = this.cmdAry.split(",");
                            ss2 = iAry;
                            var4 = iAry.length;

                            for (var5 = 0; var5 < var4; ++var5) {
                                i1 = ss2[var5];
                                i1 = i1.trim();
                                this.indexS.add(Integer.parseInt(i1));
                            }
                        }
                    } else if (this.cmdAry.indexOf("'") >= 0) {
                        this.name = this.cmdAry.substring(1, this.cmdAry.length() - 1);
                    } else if (StringUtil.isInteger(this.cmdAry)) {
                        this.start = Integer.parseInt(this.cmdAry);
                        this.ranged = true;
                    }
                }
            }

            if (!"$".equals(this.cmd) && !"@".equals(this.cmd)) {
                if (this.cmd.startsWith("^")) {
                    this.handler = JsonPath.handler_xx;
                } else if ("*".equals(this.cmd)) {
                    this.handler = JsonPath.handler_x;
                } else {
                    if (this.cmd.endsWith("]")) {
                        if ("*".equals(this.cmdAry)) {
                            this.handler = JsonPath.handler_ary_x;
                            return;
                        }

                        if (this.cmd.startsWith("?")) {
                            this.handler = JsonPath.handler_ary_exp;
                        } else if (this.cmdAry.indexOf(",") > 0) {
                            this.handler = JsonPath.handler_ary_multi;
                        } else if (this.cmdAry.indexOf(":") >= 0) {
                            this.handler = JsonPath.handler_ary_range;
                        } else if (!this.cmdAry.startsWith("$.") && !this.cmdAry.startsWith("@.")) {
                            this.handler = JsonPath.handler_ary_prop;
                        } else {
                            this.handler = JsonPath.handler_ary_ref;
                        }
                    } else if (this.cmd.endsWith(")")) {
                        this.handler = JsonPath.handler_fun;
                    } else {
                        this.handler = JsonPath.handler_prop;
                    }

                }
            } else {
                this.handler = JsonPath.handler_$;
            }
        }

        public int length() {
            return this.cmd.length();
        }

        @Override
        public String toString() {
            return this.cmd;
        }
    }

    @FunctionalInterface
    private interface Resolver {
        ONode run(Segment bef, Segment s, Boolean regroup, ONode root, ONode tmp, Boolean usd, CRUD crud);
    }
}
