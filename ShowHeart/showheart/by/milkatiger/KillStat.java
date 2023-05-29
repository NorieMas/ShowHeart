package showheart.by.milkatiger;

import org.bukkit.Bukkit;

import java.util.UUID;

public class KillStat {
    private UUID _id;
    private Long _stat;
    public KillStat _next;
    public KillStat _prev;


    public KillStat(UUID id) {
        _id = id;
        _stat = 0L;
    }

    public KillStat(UUID id, long amount) {
        _id = id;
        _stat = amount;
    }

    public void addOne() {
        _stat++;
    }

    public UUID id() {
        return _id;
    }

    public long value() {
        return _stat;
    }

    public void swapNext() {
        if (_next == null) {
            return;
        }
        if (_prev != null)
            _prev._next = _next;
        _next._prev = _prev;
        _prev = _next;
        _next = _next._next;
        _prev._next = this;
        if (_next != null)
            _next._prev = this;
    }

}
