package showheart.by.milkatiger;

public class KillStatList {
    public KillStat First, Last;

    public void clear() {
        First = null;
        Last = null;
    }
    public void addLast(KillStat ks){
        if (First == null){
            Last = First = ks;
        }else {
            ks._prev = Last;
            Last._next = ks;
            Last = ks;
        }
    }
    public KillStat get(int index){
        if (First == null){
            return null;
        }
        KillStat temp = First;
        while(index > 0) {
            temp = temp._next;
            if (temp == null){
                return null;
            }
            index--;
        }
        return temp;
    }
}
