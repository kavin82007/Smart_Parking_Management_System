public class ParkingQA {

    static int passed = 0;
    static int failed = 0;

    static void check(
            String testName,
            boolean condition) {

        if (condition) {
            System.out.println(
                    "[PASS] " + testName);
            passed++;
        } else {
            System.out.println(
                    "[FAIL] " + testName);
            failed++;
        }
    }

    // Reset all slots before each independent test
    static void resetParking() {

        for (ParkingManagement.Slot slot :
                ParkingManagement.slots) {

            slot.occupied = false;
            slot.vehicleNumber = "";
        }
    }

    static ParkingManagement.Vehicle vehicle(
            String number,
            String type,
            boolean vip,
            boolean electric) {

        return new ParkingManagement.Vehicle(
                number,
                type,
                vip,
                electric);
    }

    public static void main(String[] args) {

        System.out.println(
                "========== SMART PARKING QA ==========\n");


        // 1. Successful vehicle entry
        try {

            resetParking();

            ParkingManagement.Vehicle v =
                    vehicle(
                            "CAR001",
                            "Car",
                            false,
                            false);

            ParkingManagement.ParkingTicket t =
                    ParkingManagement.enterVehicle(
                            v,
                            "T001");

            check(
                    "Successful vehicle entry",
                    t != null &&
                    t.slotNumber >= 3 &&
                    t.slotNumber <= 4);

        } catch (Exception e) {
            check(
                    "Successful vehicle entry",
                    false);
        }


        // 2. Automatic slot allocation
        try {

            resetParking();

            ParkingManagement.Vehicle v =
                    vehicle(
                            "BIKE001",
                            "Bike",
                            false,
                            false);

            ParkingManagement.ParkingTicket t =
                    ParkingManagement.enterVehicle(
                            v,
                            "T002");

            check(
                    "Automatic slot allocation",
                    t.slotNumber == 1
                    || t.slotNumber == 2);

        } catch (Exception e) {
            check(
                    "Automatic slot allocation",
                    false);
        }


        // 3. Wrong vehicle-slot combination
        try {

            resetParking();

            ParkingManagement.Slot carSlot =
                    ParkingManagement.slots[2];

            double fee =
                    ParkingManagement.calculateParkingFee(
                            carSlot,
                            2,
                            false);

            check(
                    "Correct vehicle-slot pricing",
                    fee == 100);

        } catch (Exception e) {
            check(
                    "Correct vehicle-slot pricing",
                    false);
        }


        // 4. Duplicate vehicle
        try {

            resetParking();

            ParkingManagement.Vehicle v =
                    vehicle(
                            "DUP001",
                            "Car",
                            false,
                            false);

            ParkingManagement.enterVehicle(
                    v,
                    "T004");

            ParkingManagement.enterVehicle(
                    v,
                    "T005");

            check(
                    "Duplicate vehicle prevention",
                    false);

        } catch (IllegalStateException e) {

            check(
                    "Duplicate vehicle prevention",
                    true);
        }


        // 5. Vehicle exit
        try {

            resetParking();

            ParkingManagement.Vehicle v =
                    vehicle(
                            "EXIT001",
                            "Car",
                            false,
                            false);

            ParkingManagement.ParkingTicket t =
                    ParkingManagement.enterVehicle(
                            v,
                            "T006");

            double fee =
                    ParkingManagement.exitVehicle(
                            t,
                            2,
                            false);

            check(
                    "Vehicle exit",
                    fee == 100 && !t.active);

        } catch (Exception e) {
            check(
                    "Vehicle exit",
                    false);
        }


        // 6. Lost ticket
        try {

            resetParking();

            double charge =
                    ParkingManagement.lostTicketCharge(
                            "Car");

            check(
                    "Lost ticket handling",
                    charge == 500);

        } catch (Exception e) {
            check(
                    "Lost ticket handling",
                    false);
        }


        // 7. Early exit
        try {

            resetParking();

            ParkingManagement.Vehicle v =
                    vehicle(
                            "EARLY001",
                            "Bike",
                            false,
                            false);

            ParkingManagement.ParkingTicket t =
                    ParkingManagement.enterVehicle(
                            v,
                            "T007");

            double fee =
                    ParkingManagement.exitVehicle(
                            t,
                            1,
                            false);

            check(
                    "Early exit",
                    fee == 20);

        } catch (Exception e) {
            check(
                    "Early exit",
                    false);
        }


        // 8. Overnight parking
        try {

            resetParking();

            ParkingManagement.Vehicle v =
                    vehicle(
                            "NIGHT001",
                            "Car",
                            false,
                            false);

            ParkingManagement.ParkingTicket t =
                    ParkingManagement.enterVehicle(
                            v,
                            "T008");

            double fee =
                    ParkingManagement.exitVehicle(
                            t,
                            12,
                            false);

            check(
                    "Overnight parking",
                    fee == 600);

        } catch (Exception e) {
            check(
                    "Overnight parking",
                    false);
        }


        // 9. Peak-hour pricing
        try {

            resetParking();

            ParkingManagement.Slot slot =
                    ParkingManagement.slots[2];

            double normal =
                    ParkingManagement.calculateParkingFee(
                            slot,
                            2,
                            false);

            double peak =
                    ParkingManagement.calculateParkingFee(
                            slot,
                            2,
                            true);

            check(
                    "Peak-hour pricing",
                    peak > normal);

        } catch (Exception e) {
            check(
                    "Peak-hour pricing",
                    false);
        }


        // 10. EV parking
        try {

            resetParking();

            ParkingManagement.Vehicle v =
                    vehicle(
                            "EV001",
                            "Electric",
                            false,
                            true);

            ParkingManagement.ParkingTicket t =
                    ParkingManagement.enterVehicle(
                            v,
                            "T010");

            check(
                    "EV slot allocation",
                    t.slotNumber == 8
                    || t.slotNumber == 9);

        } catch (Exception e) {
            check(
                    "EV slot allocation",
                    false);
        }


        // 11. EV charging fee
        try {

            double fee =
                    ParkingManagement.calculateEVChargingFee(
                            10);

            check(
                    "EV charging fee",
                    fee == 150);

        } catch (Exception e) {
            check(
                    "EV charging fee",
                    false);
        }


        // 12. VIP parking
        try {

            resetParking();

            ParkingManagement.Vehicle v =
                    vehicle(
                            "VIP001",
                            "Car",
                            true,
                            false);

            ParkingManagement.ParkingTicket t =
                    ParkingManagement.enterVehicle(
                            v,
                            "T012");

            check(
                    "VIP parking",
                    t.slotNumber == 10);

        } catch (Exception e) {
            check(
                    "VIP parking",
                    false);
        }


        // 13. Full parking lot
        try {

            resetParking();

            // Fill all slots
            for (int i = 0; i < 10; i++) {

                ParkingManagement.Slot slot =
                        ParkingManagement.slots[i];

                slot.occupied = true;
                slot.vehicleNumber =
                        "FULL" + i;
            }

            ParkingManagement.Vehicle v =
                    vehicle(
                            "FULLTEST",
                            "Car",
                            false,
                            false);

            ParkingManagement.enterVehicle(
                    v,
                    "T013");

            check(
                    "Full parking lot",
                    false);

        } catch (IllegalStateException e) {

            check(
                    "Full parking lot",
                    true);
        }


        // 14. Invalid vehicle type
        try {

            resetParking();

            ParkingManagement.Vehicle v =
                    vehicle(
                            "INVALID001",
                            "Helicopter",
                            false,
                            false);

            ParkingManagement.enterVehicle(
                    v,
                    "T014");

            check(
                    "Invalid vehicle type",
                    false);

        } catch (IllegalArgumentException e) {

            check(
                    "Invalid vehicle type",
                    true);
        }


        // 15. Invalid vehicle number
        try {

            resetParking();

            ParkingManagement.Vehicle v =
                    vehicle(
                            "",
                            "Car",
                            false,
                            false);

            ParkingManagement.enterVehicle(
                    v,
                    "T015");

            check(
                    "Invalid vehicle number",
                    false);

        } catch (IllegalArgumentException e) {

            check(
                    "Invalid vehicle number",
                    true);
        }


        // 16. Lost ticket invalid vehicle type
        try {

            ParkingManagement.lostTicketCharge(
                    "Helicopter");

            check(
                    "Invalid lost-ticket input",
                    false);

        } catch (IllegalArgumentException e) {

            check(
                    "Invalid lost-ticket input",
                    true);
        }


        // 17. Negative parking duration
        try {

            resetParking();

            ParkingManagement.Slot slot =
                    ParkingManagement.slots[2];

            ParkingManagement.calculateParkingFee(
                    slot,
                    -2,
                    false);

            check(
                    "Negative parking duration",
                    false);

        } catch (IllegalArgumentException e) {

            check(
                    "Negative parking duration",
                    true);
        }


        // 18. Negative EV charging units
        try {

            ParkingManagement.calculateEVChargingFee(
                    -5);

            check(
                    "Negative EV charging input",
                    false);

        } catch (IllegalArgumentException e) {

            check(
                    "Negative EV charging input",
                    true);
        }


        // 19. Peak-hour exact calculation
        try {

            resetParking();

            ParkingManagement.Slot slot =
                    ParkingManagement.slots[2];

            double fee =
                    ParkingManagement.calculateParkingFee(
                            slot,
                            2,
                            true);

            // Car = Rs.50/hour
            // 2 hours = Rs.100
            // Peak = 25%
            // Final = Rs.125
            check(
                    "Peak-hour fee calculation",
                    Math.abs(fee - 125) < 0.01);

        } catch (Exception e) {
            check(
                    "Peak-hour fee calculation",
                    false);
        }


        // 20. Seat/slot restoration after exit
        try {

            resetParking();

            ParkingManagement.Vehicle v =
                    vehicle(
                            "RESTORE001",
                            "SUV",
                            false,
                            false);

            ParkingManagement.ParkingTicket t =
                    ParkingManagement.enterVehicle(
                            v,
                            "T020");

            int occupiedSlot =
                    t.slotNumber;

            ParkingManagement.exitVehicle(
                    t,
                    2,
                    false);

            boolean freeAgain = false;

            for (ParkingManagement.Slot slot :
                    ParkingManagement.slots) {

                if (slot.slotNumber == occupiedSlot
                        && !slot.occupied) {

                    freeAgain = true;
                    break;
                }
            }

            check(
                    "Slot restored after exit",
                    freeAgain);

        } catch (Exception e) {
            check(
                    "Slot restored after exit",
                    false);
        }


        // Final result
        System.out.println(
                "\n======================================");

        System.out.println(
                "Tests Passed : " + passed);

        System.out.println(
                "Tests Failed : " + failed);

        System.out.println(
                "Total Tests  : " + (passed + failed));

        if (failed == 0) {

            System.out.println(
                    "QA RESULT    : ALL TESTS PASSED");

        } else {

            System.out.println(
                    "QA RESULT    : SOME TESTS FAILED");

            // Jenkins detects build failure
            System.exit(1);
        }

        System.out.println(
                "======================================");
    }
}