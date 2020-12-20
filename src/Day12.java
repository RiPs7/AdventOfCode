import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Day12 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day12.txt");
        final List<Command> commands = lines.stream().map(Command::new).collect(Collectors.toList());

        final Ship ship = new SimpleShip();
        for (final Command command : commands) {
            ship.apply(command);
        }
        System.out.println("The ship's position after the commands is: " +
            "(" + Math.round(ship.pos.getX()) + "," + Math.round(ship.pos.getY()) + "). " +
            "The Manhattan distance from its origin is: " +
            (Math.round(Math.abs(ship.pos.getX())) + Math.round(Math.abs(ship.pos.getY()))) + " units.");
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day12.txt");
        final List<Command> commands = lines.stream().map(Command::new).collect(Collectors.toList());

        final ShipWithWayPoint shipWithWayPoint = new ShipWithWayPoint();
        for (final Command command : commands) {
            shipWithWayPoint.apply(command);
        }
        System.out.println("The ship's position after the commands is: " +
            "(" + Math.round(shipWithWayPoint.pos.getX()) + "," + Math.round(shipWithWayPoint.pos.getY()) + "). " +
            "The Manhattan distance from its origin is: " +
            (Math.round(Math.abs(shipWithWayPoint.pos.getX())) + Math.round(Math.abs(shipWithWayPoint.pos.getY()))) + " units.");
    }

    private static abstract class Ship {
        Point2D pos;

        abstract void apply(Command command);

        abstract void move(double dx, double dy);

        abstract void rotate(int degrees);

        abstract void forward(int value);
    }

    private static final class SimpleShip extends Ship {
        int dir;

        public SimpleShip() {
            pos = new Point2D.Double(0, 0);
            dir = 0; // facing east
        }

        void apply(final Command command) {
            command.apply(this);
        }

        void move(double dx, double dy) {
            pos.setLocation(pos.getX() + dx, pos.getY() + dy);
        }

        void rotate(int degrees) {
            dir += degrees;
        }

        void forward(int value) {
            double dx = Math.cos(Math.toRadians(dir)) * value;
            double dy = Math.sin(Math.toRadians(dir)) * value;
            move(dx, dy);
        }
    }

    private static final class ShipWithWayPoint extends Ship {
        Point2D wayPoint;
        double wayPointRads;

        public ShipWithWayPoint() {
            pos = new Point2D.Double(0, 0);
            wayPoint = new Point2D.Double(10, -1);
            wayPointRads = Math.atan2(-1, 10);
        }

        void apply(final Command command) {
            command.apply(this);
        }

        void move(double dx, double dy) {
            wayPoint.setLocation(wayPoint.getX() + dx, wayPoint.getY() + dy);
            wayPointRads = Math.atan2(wayPoint.getY(), wayPoint.getX());
        }

        void rotate(int degrees) {
            wayPointRads += Math.toRadians(degrees);
            double distToWayPoint = Math.sqrt(wayPoint.getX() * wayPoint.getX() + wayPoint.getY() * wayPoint.getY());
            wayPoint.setLocation(Math.cos(wayPointRads) * distToWayPoint, Math.sin(wayPointRads) * distToWayPoint);
        }

        void forward(int value) {
            pos.setLocation(pos.getX() + value * wayPoint.getX(), pos.getY() + value * wayPoint.getY());
        }
    }

    private static final class Command {
        char action;
        int value;

        public Command(final String command) {
            action = command.charAt(0);
            value = Integer.parseInt(command.substring(1));
        }

        public void apply(final Ship ship) {
            switch (action) {
                case 'N':
                    ship.move(0, -value);
                    break;
                case 'S':
                    ship.move(0, value);
                    break;
                case 'E':
                    ship.move(value, 0);
                    break;
                case 'W':
                    ship.move(-value, 0);
                    break;
                case 'L':
                    ship.rotate(-value);
                    break;
                case 'R':
                    ship.rotate(value);
                    break;
                case 'F':
                    ship.forward(value);
                    break;
                default:
                    break;
            }
        }
    }
}
