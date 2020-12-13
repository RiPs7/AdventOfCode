import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day4 extends Day {

    void part1() throws IOException {
        final List<String> lines = Utils.readLines("day4.txt");
        final List<Passport> passports = extractPassports(lines);
        int valid = 0;
        for (Passport passport : passports) {
            if (passport.isValid()) {
                valid++;
            }
        }
        System.out.println("There are " + valid + " valid passports");
    }

    void part2() throws IOException {
        final List<String> lines = Utils.readLines("day4.txt");
        final List<Passport> passports = extractPassports(lines);
        int strictlyValid = 0;
        for (Passport passport : passports) {
            if (passport.isStrictlyValid()) {
                strictlyValid++;
            }
        }
        System.out.println("There are " + strictlyValid + " strictly valid passports");
    }

    private List<Passport> extractPassports(final List<String> lines) {
        final List<String> allPassportLines = new ArrayList<>();
        StringBuilder passportLine = new StringBuilder();
        for (final String currentLine : lines) {
            if (isNotBlank(currentLine)) {
                passportLine.append(currentLine).append(" ");
            } else {
                allPassportLines.add(passportLine.toString().trim());
                passportLine = new StringBuilder();
            }
        }
        allPassportLines.add(passportLine.toString().trim());
        return allPassportLines.stream().map(pl -> {
            final Passport passport = new Passport();
            final String[] parts = pl.split(" ");
            for (String part : parts) {
                String[] keyValue = part.split(":");
                switch (keyValue[0]) {
                    case "byr":
                        passport.byr = keyValue[1];
                        break;
                    case "iyr":
                        passport.iyr = keyValue[1];
                        break;
                    case "eyr":
                        passport.eyr = keyValue[1];
                        break;
                    case "hgt":
                        passport.hgt = keyValue[1];
                        break;
                    case "hcl":
                        passport.hcl = keyValue[1];
                        break;
                    case "ecl":
                        passport.ecl = keyValue[1];
                        break;
                    case "pid":
                        passport.pid = keyValue[1];
                        break;
                    case "cid":
                        passport.cid = keyValue[1];
                        break;
                    default:
                        throw new RuntimeException("Unknown field: " + keyValue[0]);
                }
            }
            return passport;
        }).collect(Collectors.toList());
    }

    private static final class Passport {
        String byr;
        String iyr;
        String eyr;
        String hgt;
        String hcl;
        String ecl;
        String pid;
        String cid;

        boolean isValid() {
            return isNotBlank(this.byr) &&
                isNotBlank(this.iyr) &&
                isNotBlank(this.eyr) &&
                isNotBlank(this.hgt) &&
                isNotBlank(this.hcl) &&
                isNotBlank(this.ecl) &&
                isNotBlank(this.pid);
        }

        boolean isStrictlyValid() {
            if (!isValid()) {
                return false;
            }
            final boolean isByrValid = this.byr.matches("\\d{4}") &&
                Integer.parseInt(this.byr) >= 1920 &&
                Integer.parseInt(this.byr) <= 2002;
            final boolean isIyrValid = this.iyr.matches("\\d{4}") &&
                Integer.parseInt(this.iyr) >= 2010 &&
                Integer.parseInt(this.iyr) <= 2020;
            final boolean isEyrValid = this.eyr.matches("\\d{4}") &&
                Integer.parseInt(this.eyr) >= 2020 &&
                Integer.parseInt(this.eyr) <= 2030;
            final boolean isHgtValid =
                (this.hgt.matches("\\d+cm") &&
                Integer.parseInt(this.hgt.substring(0, this.hgt.length() - 2)) >= 150 &&
                Integer.parseInt(this.hgt.substring(0, this.hgt.length() - 2)) <= 193) ||
                (this.hgt.matches("\\d+in") &&
                Integer.parseInt(this.hgt.substring(0, this.hgt.length() - 2)) >= 59 &&
                Integer.parseInt(this.hgt.substring(0, this.hgt.length() - 2)) <= 76);
            final boolean isHclValid = this.hcl.matches("#[\\da-f]{6}");
            final boolean isEclValid = this.ecl.matches("(amb|blu|brn|gry|grn|hzl|oth)");
            final boolean isPidValid = this.pid.matches("\\d{9}");
            return isByrValid &&
                isIyrValid &&
                isEyrValid &&
                isHgtValid &&
                isHclValid &&
                isEclValid &&
                isPidValid;
        }
    }

    private static boolean isNotBlank(final String s) {
        return s != null && !s.equals("");
    }

}
