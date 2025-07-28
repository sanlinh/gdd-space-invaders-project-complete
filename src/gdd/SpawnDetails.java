package gdd;


public class SpawnDetails {
    public String type;
    public int x;
    public int y;
    public int count  = 1;
    public int spacing =0;
    public int interval = 0;

    public SpawnDetails(String type, int x, int y) {
      this.type = type;
      this.x = x;
      this.y = y;
    }

    public SpawnDetails(String type, int x, int y, int count, int spacing, int interval) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.count = count;
        this.spacing = spacing;
        this.interval = interval;
    }
}
