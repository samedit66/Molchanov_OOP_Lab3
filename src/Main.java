public class Main {

    public static void main(String[] args) {
        IpAddress x = new IpAddress(new int[]{ 127, 1, 0, 1 });
        IpAddress y = new IpAddress(new int[]{ 127, 1, 0, 19 });
        IpAddress mask = new IpAddress(new int[]{ 255, 255, 255, 0 });

        System.out.println(x.countHostsBetween(y, mask));
    }
}