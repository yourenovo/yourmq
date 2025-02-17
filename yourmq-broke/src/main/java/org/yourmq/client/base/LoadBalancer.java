//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.client.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class LoadBalancer {
    private static AtomicInteger roundCounter = new AtomicInteger(0);

    public LoadBalancer() {
    }

    private static int roundCounterGet() {
        int counter = roundCounter.incrementAndGet();
        if (counter > 999999) {
            roundCounter.set(0);
        }

        return counter;
    }

    public static <T extends ClientSession> T getAnyByPoll(Collection<T> coll) {
        return getAny(coll, LoadBalancer::roundCounterGet);
    }

    public static <T extends ClientSession> T getAnyByHash(Collection<T> coll, String diversion) {
        diversion.getClass();
        return getAny(coll, diversion::hashCode);
    }

    public static <T extends ClientSession> T getAny(Collection<T> coll, Supplier<Integer> randomSupplier) {
        if (coll != null && coll.size() != 0) {
            List<T> sessions = new ArrayList();
            Iterator var3 = coll.iterator();

            while(var3.hasNext()) {
                T s = (T) var3.next();
                if (SessionUtils.isActive(s)) {
                    sessions.add(s);
                }
            }

            if (sessions.size() == 0) {
                return null;
            } else if (sessions.size() == 1) {
                return (T) sessions.get(0);
            } else {
                int random = Math.abs((Integer)randomSupplier.get());
                int idx = random % sessions.size();
                return (T) sessions.get(idx);
            }
        } else {
            return null;
        }
    }

    public static <T extends ClientSession> T getFirst(Collection<T> coll) {
        if (coll != null && coll.size() != 0) {
            Iterator var1 = coll.iterator();

            ClientSession s;
            do {
                if (!var1.hasNext()) {
                    return null;
                }

                s = (ClientSession)var1.next();
            } while(!SessionUtils.isActive(s));

            return (T) s;
        } else {
            return null;
        }
    }
}
