package net.pixlies.proxy.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ProxyQueuePlayer implements Comparable<ProxyQueuePlayer> {

    private final UUID uuid;
    private int priority;
    private long insertedMillis;

    @Override
    public int compareTo(@NotNull ProxyQueuePlayer o) {
        int result = -Integer.compare(priority, o.getPriority());

        if (result == 0) {
            return Long.compare(insertedMillis, o.getInsertedMillis());
        }

        return result;
    }

}
